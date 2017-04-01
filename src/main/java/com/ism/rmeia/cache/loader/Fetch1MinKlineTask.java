package com.ism.rmeia.cache.loader;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map.Entry;

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
import com.ism.market.KLine;
import com.ism.rmeia.cache.SimpleCache;

public  class Fetch1MinKlineTask implements Runnable {
    private final static Logger logger = LoggerFactory.getLogger(SimpleCacheLoader.class);
   
    SimpleCache cache;
    String url;
    String host;
    int port;
    public Fetch1MinKlineTask() {
        url = "http://localhost:58080/rtpa/macd/period/1/stocks";
    }


    @Override
    public void run() {
        try {
            Calendar now = Calendar.getInstance();
            int hour = now.get(Calendar.HOUR_OF_DAY);
            if (hour < 9) {
                return;
            }
            // 创建HttpClient实例
            CloseableHttpClient httpclient = HttpClients.createDefault();
            // 创建Get方法实例
            HttpGet httpgets = new HttpGet(url);
            CloseableHttpResponse response = null;
            try {
                response = httpclient.execute(httpgets);
                String strResult = "";
                if (response.getStatusLine().getStatusCode() == 200) {
                    strResult = EntityUtils.toString(response.getEntity());
                     JSONObject json = JSON.parseObject(strResult);
                     
                     for(Entry<String, Object> e:json.entrySet()){
                        ArrayList<KLine> klines = new ArrayList<>(256);
                        String stockCode = e.getKey();
                        
                        JSONArray klineObj  = (JSONArray)(e.getValue());
                        for(int i=0;i<klineObj.size();i++){
                            JSONObject klineJ = (JSONObject) klineObj.get(i);
                            KLine kline = JSON.toJavaObject(klineJ, KLine.class);
                            klines.add(kline);
                        }
                        SimpleCacheLoader.getCache().putKLines(stockCode, klines);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("[Fetch1MinKline] ,ex={}", e);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            logger.info("Fetch1MinKline failed {}", t);
        }
    }
    
    public void setUrl(String url) {
        this.url = url;
    }


    public SimpleCache getCache() {
        return cache;
    }


    public void setCache(SimpleCache cache) {
        this.cache = cache;
    }


    public String getUrl() {
        return url;
    }

}