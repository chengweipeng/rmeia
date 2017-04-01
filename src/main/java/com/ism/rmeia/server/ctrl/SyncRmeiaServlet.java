package com.ism.rmeia.server.ctrl;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ism.market.KLine;
import com.ism.market.hbase.KLineGetUtil;
import com.ism.rmeia.SignalTask;
import com.ism.rmeia.bean.InputIAInfo;
import com.ism.rmeia.bean.StockSignal;
import com.ism.rmeia.output.WriterStockInstructionProducer;
import com.ism.rmeia.task.GetSignalTimer;
import com.ism.rmeia.task.MessageConsume;

/**
 * 读取IA发送过来的交易数据
 * 这个是个同步请求并返回的
 *
 * @author wx
 */
@SuppressWarnings("serial")
public class SyncRmeiaServlet extends HttpServlet {
    private final static Logger logger = LoggerFactory.getLogger(SignalTask.class);

   
    public SyncRmeiaServlet() {
    }

    /**
     * 需要把异步变成同步，因此需要future部分
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("Application/json");
        PrintWriter wr = response.getWriter();
        InputStream is = request.getInputStream();
        WriterStockInstructionProducer producer = new WriterStockInstructionProducer();
        try {
            MessageConsume consumer = null;
            String json = EntityUtils.toString(new InputStreamEntity(is), StandardCharsets.UTF_8);
            JSONArray arr = JSON.parseArray(json);
            int i = 0;
            
            List<InputIAInfo> inputIAs = JSON.parseArray(json, InputIAInfo.class);
            if (inputIAs != null) {
                logger.info("input ia size={}", inputIAs.size());
                for (InputIAInfo inputIA : inputIAs) {
                    if (inputIA != null) {
                        try {
                            Map<String, StockSignal> sigMap = new HashMap<String, StockSignal>(GetSignalTimer.getInstance().getSIGE());
                            //Map<String,ArrayList<KLine>> kLineMap = KLineGetUtil.getKlineMap();
                            Map<String,List<KLine>> kLineMap = new HashMap<>();
                            JSONObject inputIAObject = arr.getJSONObject(i++);
                            JSONArray stockArray = inputIAObject.getJSONArray("stock");
                            for (int index = 0; index < stockArray.size(); index++) {
                                JSONObject stockObject = stockArray.getJSONObject(index);
                                StockSignal sig = JSON.toJavaObject(stockObject, StockSignal.class);
                                sigMap.put(sig.stock_id, sig);
                                
                                ArrayList<KLine> listKline = new ArrayList<>();
                                JSONArray klineArray = inputIAObject.getJSONArray("kline");
                                for(int kindex = 0;kindex < klineArray.size(); kindex++){
                                    JSONObject klineObject = klineArray.getJSONObject(kindex);
                                    KLine kline = JSON.toJavaObject(klineObject, KLine.class);
                                    listKline.add(kline);   
                                }
                                kLineMap.put(sig.stock_id, listKline);
                            }
                            logger.debug("input ia={}", inputIA);
                            consumer = new MessageConsume(inputIA, sigMap,kLineMap);
                            producer.setWriter(wr);
                            consumer.setProducer(producer);
                            Long opTime = inputIAObject.getLong("request_tm");
                            if(opTime!=null){
                                consumer.setOperationTime(opTime*1000);
                            }
                            consumer.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                // 输入为空
                wr.write(RenderJsonUtil.getStatusJson(500, "no IA input."));
            }
           if(consumer !=null){
               //同步
               Future<?>f = consumer.getFuture();
               if(f!=null && !f.isDone()){
                   f.get();
                   //f.get(1000, TimeUnit.MILLISECONDS);
               }
           }
            response.flushBuffer();
        } catch (Throwable t) {
            t.printStackTrace();
            // logger.info(t.getMessage());
        } finally {
            IOUtils.closeQuietly(wr);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
