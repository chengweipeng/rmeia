package com.ism.rmeia.server.ctrl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.ism.rmeia.bean.PredStock;
import com.ism.rmeia.query.PredictStockQuery;
import com.ism.rmeia.task.GetPredictTimer;

@SuppressWarnings("serial")
public class PredictStockServlet extends HttpServlet {
    private final static Logger logger = LoggerFactory.getLogger(PredictStockServlet.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/json");
        OutputStream os = null;
        Map<String, PredStock> map = GetPredictTimer.getInstance().getCurrentPredictMap();
        String vers = request.getParameter("vers");
        String fmt = request.getParameter("fmt");
        String code = request.getParameter("code");
        boolean supportGzip = true;
        String json="";
        logger.info("remoteAddr={},vers={},fmt={},code={}",request.getRemoteAddr(),vers,fmt,code);
        try {
            os = response.getOutputStream();
            String[] stocks = StringUtils.split(code, ",");
            if (stocks == null) {
                json = RenderJsonUtil.getStatusJson(403, "stock code is null ");
                os.write(json.getBytes());
                return;
            }
            json = getStockJson(map,stocks);// 股票
            byte[] buffer = json.getBytes("UTF-8");
            if (supportGzip) {
                response.setHeader(HttpHeaders.CONTENT_ENCODING, "gzip");
                GZIPOutputStream gzipOut = new GZIPOutputStream(os);
                gzipOut.write(buffer);
                gzipOut.close();
            } else {
                os.write(buffer);
            }
            response.flushBuffer();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            IOUtils.closeQuietly(os);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
    public String getStockJson(Map<String, PredStock> map, String ...stocks){
        PredictStockQuery query = new PredictStockQuery();
        Map<String,PredStock> retMap = query.getPredictStock(map, stocks);
        return JSON.toJSONString(retMap);
    }
}
