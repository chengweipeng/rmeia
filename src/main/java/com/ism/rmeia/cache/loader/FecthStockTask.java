package com.ism.rmeia.cache.loader;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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

public class FecthStockTask implements Runnable {
    private final static Logger logger = LoggerFactory.getLogger(FecthStockTask.class);
    public static final java.lang.String yyyyMMdd_FORMAT = "yyyyMMdd";
    
    SimpleCache cache;
    
    String url;
    public FecthStockTask() {
    }

    @Override
    public void run() {

        SimpleDateFormat sdf = new SimpleDateFormat(yyyyMMdd_FORMAT);
        try {
            Calendar now = Calendar.getInstance();
            int hour = now.get(Calendar.HOUR_OF_DAY);
            if (hour < 9) {
                return;
            }
            String yyyyMMdd = sdf.format(now.getTime());
            // get 指数
            float hs300 = 0;// TODO 查询

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
                    JSONArray array = JSON.parseArray(strResult);
                    JSONObject obj = (JSONObject)(array.get(0));
                    BigDecimal last_price = (BigDecimal)(obj.get("last_price"));
                    hs300 = last_price.floatValue();
                }
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("[stock:price] ,ex={}", e);
            }
            cache.putHs300(yyyyMMdd, hs300);
        } catch (Throwable t) {
            t.printStackTrace();
            logger.info("cache load failed {}", t);
        }
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

    public void setUrl(String url) {
        this.url = url;
    }
    
}