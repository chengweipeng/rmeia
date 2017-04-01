package com.ism.rmeia.cache.loader;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ism.rmeia.cache.SimpleCache;

public class InitCacheTask implements Runnable {
    private final static Logger logger = LoggerFactory.getLogger(InitCacheTask.class);
    public static final java.lang.String yyyyMMdd_FORMAT = "yyyyMMdd";

    SimpleCache cache;

    String hqHS300url;
    public InitCacheTask() {
    }

    @Override
    public void run() {
        initHS300Cache();
    }

    /**
     * 
     */
    private void initHS300Cache() {
        SimpleDateFormat sdf = new SimpleDateFormat(yyyyMMdd_FORMAT);
        try {
            Calendar startDate = Calendar.getInstance();
            startDate.add(Calendar.DAY_OF_MONTH, -100);

            String yyyyMMdd = sdf.format(startDate.getTime());
            // get 指数
            // 创建HttpClient实例
            CloseableHttpClient httpclient = HttpClients.createDefault();
            // 创建Get方法实例
            String url = hqHS300url + "&start=" + yyyyMMdd;
            
            logger.info("[stock:price] ,hs300.url={}", url);
            CloseableHttpResponse response = null;
            try {
                HttpGet httpgets = new HttpGet(url);
                
                response = httpclient.execute(httpgets);
                String strResult = "";
                
                if (response.getStatusLine().getStatusCode() == 200) {
                    strResult = EntityUtils.toString(response.getEntity());
                    //logger.info("[stock:price] ,info={}", strResult);
                    JSONObject jsonObj = JSON.parseObject(strResult);
                    JSONObject results = jsonObj.getJSONObject("results");
                    JSONObject hs300Json = results.getJSONObject("000300");
                    JSONArray arr = hs300Json.getJSONArray("price");
                    for (int i = 0; i < arr.size(); i++) {
                        JSONObject prieceObj = arr.getJSONObject(i);
                        Float hs300 = prieceObj.getFloat("close");
                        String strDate = prieceObj.getString("date");
                        cache.putHs300(strDate, hs300);
                    }
                }
                               
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("[initHS300Cache] ,ex={}", e);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            logger.info("initHS300Cache failed {}", t);
        }
    }

    public SimpleCache getCache() {
        return cache;
    }

    public void setCache(SimpleCache cache) {
        this.cache = cache;
    }

    public void configure(Map<Object,Object> config){
        this.hqHS300url = (String)config.get("price.hs300.history.url");
        //this.hqHS300url = (String)config.get("price.hs300.url");
    }

}