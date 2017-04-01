package com.ism.rmeia.task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.google.gson.stream.JsonWriter;
import com.ism.util.HttpClientUtils;
import com.ism.util.TimeUtil;

public class GetStockPriceTimer extends TimerTask implements Runnable {
    private final static Logger logger = LoggerFactory
            .getLogger(GetStockPriceTimer.class);
    private static GetStockPriceTimer me;
    // @Value("${price.getstockprice}")
    String batch_url;
    // @Value("${price.getall}")
    String all_stock_url;
    RESPONSE_TYPE type;
    Map<String, Integer> latestPriceMap = new ConcurrentHashMap<>();
    public GetStockPriceTimer() {
        type = RESPONSE_TYPE.JSON;
    }

    public Map<String, Integer> getAll()
            throws ClientProtocolException, IOException {
        // http://192.168.10.156:5438/queryallstock?version=2.0&format=json
        if (StringUtils.isEmpty(all_stock_url)) {
            return Collections.emptyMap();
        }
        long operationTime = System.currentTimeMillis();

        if (TimeUtil.beforeOpen(operationTime)
                || TimeUtil.afterClose(operationTime)) {
            return Collections.emptyMap();
        }
        HashMap<String, Integer> latestPriceMap = new HashMap<>();

        String json = HttpClientUtils.getInstance().get(all_stock_url, null,
                5000, 5000, StandardCharsets.UTF_8, true);
        List<StockPrice> stocks = JSON.parseArray(json, StockPrice.class);
        for(StockPrice stockPrice:stocks) {
            float v = 0;
            try {
                v = Float.parseFloat(stockPrice.getLast_price());
            } catch (NumberFormatException nfe) {
            }
            int price = (int) (v * 100);
            latestPriceMap.put(stockPrice.getStock_code(), price);
        }
        return latestPriceMap;
    }
    public static enum RESPONSE_TYPE {
        JSON, CSV,
    }

    public GetStockPriceTimer(RESPONSE_TYPE type) {
        this.type = type;
    }

    public Map<String, Integer> getPrice(Collection<String> stocks) {
        switch (type) {
            case JSON :
                return getJsonPrice(stocks);
            default :
                throw new RuntimeException("");
        }
    }

    private Map<String, Integer> getJsonPrice(Collection<String> stocks) {
        try {
            return new GetPriceRequestJson(stocks, batch_url).getPrice();
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<String, Integer>();
        }
    }

    public static class GetPriceRequestJson {

        final Collection<String> stocks;
        String url;

        public GetPriceRequestJson(final Collection<String> stocks,
                final String url) {
            this.stocks = stocks;
            this.url = url;
        }

        /**
         * 价格单位为分
         *
         * @return (股票代码，价格（单位分）)
         * @throws IOException
         */
        public Map<String, Integer> getPrice() throws IOException {
            // HttpClientBuilder builder = HttpClientBuilder.create();
            // CloseableHttpClient httpClient = builder.build();
            Preconditions.checkNotNull(url);
            if (stocks == null || stocks.isEmpty()) {
                logger.warn("no stock select.");
                return new HashMap<String, Integer>();
            }
            URL getPriceUrl = new URL(url);
            InputStream is = null;
            HttpURLConnection con = null;
            try {
                con = (HttpURLConnection) getPriceUrl.openConnection();
                con.setDoInput(true);
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                createJson(os);
                IOUtils.closeQuietly(os);
                is = con.getInputStream();
                if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    int nLen = con.getContentLength();
                    byte[] buf = new byte[nLen];
                    int n = 0;
                    while (n < nLen) {
                        int readBytes = is.read(buf, n, nLen - n);
                        if (readBytes == -1) {
                            break;
                        } else {
                            n += readBytes;
                        }
                    }
                    if (n < nLen) {
                        logger.warn("http length is not valid.");
                    }
                    return parsePriceJson(new String(buf, 0, n, "GB18030"));
                } else {
                    logger.error("http connection error:{}",
                            con.getResponseMessage());
                }
            } finally {
                IOUtils.closeQuietly(is);
            }
            return new HashMap<String, Integer>();
        }

        private Map<String, Integer> parsePriceJson(String json) {
            // {"bizData":{"message":[{"stock_code":"600523","stock_time":"1440125275","last_price":"24.9400","open_price":"24.3000","close_price":"24.9400","high_price":"26.0000","low_price":"24.3000"},{"stock_code":"002596","stock_time":"1440125275","last_price":"23.3900","open_price":"24.4000","close_price":"23.3900","high_price":"24.8900","low_price":"23.0600"},{"stock_code":"600629","stock_time":"1440125275","last_price":"27.8000","open_price":"28.4600","close_price":"27.8000","high_price":"29.0000","low_price":"27.7000"},{"stock_code":"002493","stock_time":"1440125275","last_price":"20.4700","open_price":"20.0000","close_price":"20.4700","high_price":"20.9700","low_price":"19.7200"},{"stock_code":"600890","stock_time":"1440125275","last_price":"9.6900","open_price":"9.9400","close_price":"9.6900","high_price":"10.0800","low_price":"9.6300"},{"stock_code":"002692","stock_time":"1440125275","last_price":"18.0000","open_price":"18.5300","close_price":"18.0000","high_price":"19.0500","low_price":"17.9500"},{"stock_code":"000901","stock_time":"1440125275","last_price":"0.0000","open_price":"0.0000","close_price":"0.0000","high_price":"0.0000","low_price":"100000.0000"},{"stock_code":"002364","stock_time":"1440125275","last_price":"21.5000","open_price":"22.7400","close_price":"21.5000","high_price":"22.9900","low_price":"21.3400"}]}}
            Map<String, Integer> m = new HashMap<String, Integer>();
            JSONObject bizData = JSON.parseObject(json)
                    .getJSONObject("bizData");
            JSONArray message = bizData.getJSONArray("message");
            for (int i = 0; i < message.size(); i++) {
                JSONObject msg = message.getJSONObject(i);
                m.put(msg.getString("stock_code"),
                        (int) (Float.valueOf(msg.getString("last_price"))
                                * 100));
            }
            return m;
        }

        public void createJson(OutputStream os) throws IOException {

            JsonWriter writer = new JsonWriter(new OutputStreamWriter(os));
            writer.beginObject();
            JsonWriter request = writer.name("request");
            request.beginObject();
            request.name("source").value("ia");
            request.name("version").value("1.0");
            request.endObject();
            JsonWriter bizData = writer.name("bizData");
            JsonWriter stockWriter = bizData.beginArray();
            for (String stock : stocks) {
                stockWriter.beginObject();
                stockWriter.name("stockCode").value(stock);
                stockWriter.endObject();
            }
            bizData.endArray();
            writer.endObject();
            writer.close();
        }
    }

    /**
     * @return 批量查询股票价格
     */
    public String getBatch_url() {
        return batch_url;
    }

    public void setBatch_url(String batch_url) {
        this.batch_url = batch_url;
    }

    /**
     * @return 查询所有股票代码
     */
    public String getAll_stock_url() {
        return all_stock_url;
    }

    public void setAll_stock_url(String all_stock_url) {
        this.all_stock_url = all_stock_url;
    }

    @Override
    public void run() {
        try {
            Map<String, Integer> m = getAll();
            if (!m.isEmpty()) {
                // latestPriceMap.clear();
                latestPriceMap.putAll(m);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @return get latest map,单位分
     */
    public Map<String, Integer> getLatestPriceMap() {
        return latestPriceMap;
    }

    /**
     * only for test
     * 
     * @param latestPriceMap
     */
    public void setLatestPriceMap(Map<String, Integer> latestPriceMap) {
        this.latestPriceMap = latestPriceMap;
    }

    public static GetStockPriceTimer getInstance() {
        if (me == null) {
            synchronized (GetStockPriceTimer.class) {
                if (me == null) {
                    me = new GetStockPriceTimer();
                }
            }
        }
        return me;
    }
}
