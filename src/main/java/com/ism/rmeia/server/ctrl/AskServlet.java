package com.ism.rmeia.server.ctrl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.ism.rmeia.bean.StockSignal;
import com.ism.rmeia.bean.UserStockInstruction;
import com.ism.rmeia.query.AskStockOperationQuery;
import com.ism.rmeia.task.GetSignalTimer;
import com.ism.rmeia.task.GetStockPriceTimer;

/**
 * 专门用于查询股票买卖的同步方法
 * @author wx
 *
 */
@SuppressWarnings("serial")
public class AskServlet extends HttpServlet {
    private final static Logger logger = LoggerFactory.getLogger(AskServlet.class);
    /**
     * 输入参数
     * vers=XX&fmt=XX&code=s1,s2,s3
     * code表示股票、债券等
     * vers 版本号
     * fmt 输入格式
     * code 股票代码号
     * 
     */
    @Override
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/json;charset=UTF-8");
        OutputStream os=null;
        try {
            String vers = request.getParameter("vers");
            String fmt = request.getParameter("fmt");
            String code = request.getParameter("code");
            logger.info("request vers={},fmt={},code={}",vers,fmt,code);
            os = response.getOutputStream();
            String[] stocks = StringUtils.split(code, ",");
            String json="";
            if (stocks == null) {
                json = RenderJsonUtil.getStatusJson(HttpURLConnection.HTTP_BAD_REQUEST, "stock code is null.");
                os.write(json.getBytes());
                return;
            }
            Map<String,StockSignal> signalMap = GetSignalTimer.getInstance().getSIGE();
            Map<String,Integer> hqPriceMap = GetStockPriceTimer.getInstance().getLatestPriceMap();
            //(signalMap
            AskStockOperationQuery askStockOperationQuery = new AskStockOperationQuery();
            Map<String,UserStockInstruction> instructionMap = askStockOperationQuery.getStockOperation(signalMap,hqPriceMap,stocks);
            logger.debug(" response = {}",instructionMap);
            json = JSON.toJSONString(instructionMap);
            if(StringUtils.isEmpty(json)){
                os.write(RenderJsonUtil.getStatusJson(500, "nothing to do.").getBytes());
            } else {
                // 输入为空
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }
        } catch (Throwable t) {
            logger.info(t.getMessage());
        } finally {
            os.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request,response);
    }
    public String getStockJson(Map<String, StockSignal> signalMap, Map<String, Integer> priceMap,String ...stocks){
        AskStockOperationQuery query = new AskStockOperationQuery();
        Map<String,UserStockInstruction> retMap = query.getStockOperation(signalMap,priceMap,stocks);
        return JSON.toJSONString(retMap);
    }
}
