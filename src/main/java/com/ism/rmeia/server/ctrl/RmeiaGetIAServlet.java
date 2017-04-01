package com.ism.rmeia.server.ctrl;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.zip.GZIPInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ism.market.KLine;
import com.ism.market.hbase.KLineGetUtil;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.ism.rmeia.bean.InputIAInfo;
import com.ism.rmeia.bean.StockSignal;
import com.ism.rmeia.output.HttpStockInstructionProducer;
import com.ism.rmeia.task.GetSignalTimer;
import com.ism.rmeia.task.MessageConsume;

/**
 * 读取IA发送过来的交易数据
 *
 * @author wx
 */
@SuppressWarnings("serial")
public class RmeiaGetIAServlet extends HttpServlet {
    private final static Logger logger = LoggerFactory.getLogger(RmeiaGetIAServlet.class);

    public RmeiaGetIAServlet() {
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("Application/json");
        PrintWriter wr = null;
        InputStream is = null;
        long start = System.currentTimeMillis();
        
        try {
            wr = response.getWriter();
            is = request.getInputStream();
            // reader = new JsonReader(new
            // InputStreamReader(is,StandardCharsets.ISO_8859_1));
            String content_encoding = request.getHeader(HttpHeaders.CONTENT_ENCODING);

            if(content_encoding != null && content_encoding.toUpperCase().contains("GZIP")){
                is = new GZIPInputStream(is);
            }
            String json = EntityUtils.toString(new InputStreamEntity(is), StandardCharsets.UTF_8);
            logger.debug("receive = {}", json);
            String callurl = request.getQueryString();
            
            List<InputIAInfo> inputIAs = JSON.parseArray(json, InputIAInfo.class);
            ArrayList<Future<?>> futures = new ArrayList<>();
            if (inputIAs != null) {
                logger.info("input ia size = {}", inputIAs.size());
                for (InputIAInfo inputIA : inputIAs) {
                    if (inputIA != null) {
                        try {
                            logger.debug("input ia = {}", inputIA);
                            Map<String,StockSignal> sigMap = GetSignalTimer.getInstance().getSIGE();
                            Map<String,List<KLine>> kLineMap = KLineGetUtil.getKlineMap();
                            MessageConsume consumer = new MessageConsume(inputIA,sigMap,kLineMap);
                            //String host = request.getRemoteHost();
                            //int port = request.getRemotePort();
                            HttpStockInstructionProducer producer = new HttpStockInstructionProducer();
                            //String newURL = genReturnBackURL(HttpStockInstructionProducer.getInstance().getUrl(),host, port);
                            String url = getBackUrl(callurl,HttpStockInstructionProducer.getInstance().getUrl());
                            producer.setUrl(url);
                            consumer.setProducer(producer);
                            if(sigMap.isEmpty()){
                            	sigMap.putAll(GetSignalTimer.getInstance().getSIGE());
                            }
                            consumer.call();
                            futures.add(consumer.getFuture());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                wr.write(RenderJsonUtil.getStatusJson(0, "HTTP OK"));
            } else {
                // 输入为空
                wr.write(RenderJsonUtil.getStatusJson(500, "no IA input."));
            }
            response.flushBuffer();
//            for (Future<?> f : futures) {
//                if (f != null && !f.isDone()) {
//                    f.get(1000, TimeUnit.MILLISECONDS);
//                }
//            }
        } catch (Throwable t) {
            t.printStackTrace();
            logger.error("exception={}",t);
        } finally {
            long end = System.currentTimeMillis();
            logger.info("[RmeiaGetIA] cost={}",(end-start));
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(wr);
        }
    }

    /**
     * 
     * @param oldUrl
     * @param host
     * @param port
     * @return 需要返回的url
     * @throws MalformedURLException
     */
    public String genReturnBackURL(String oldUrl,String host, int port) throws MalformedURLException {
        
        if(logger.isDebugEnabled()){
            logger.debug("oldUrl={},host={},port={}",oldUrl,host,port);
        }
        //URL defaultURL = new URL(oldUrl);
        //return ("http://" + host + ":" + port + defaultURL.getPath());
        return oldUrl;
    }
    
    //不直接读取参数，而从backurl解析为了防止backurl中也带参数
    public static String getBackUrl(String backurl,String oldurl){

        if(backurl==null||backurl.length()==0)
            return oldurl;
        
        int index = backurl.indexOf("backurl=");
        String backnewurl = backurl.substring(index+"backurl=".length());
        
        
        boolean bool = backnewurl.startsWith("http://");
        if(!bool){
            StringBuffer sb = new StringBuffer();
            sb.append("http://");
            sb.append(backnewurl);
            return sb.toString();
        }
        return backnewurl;  
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
