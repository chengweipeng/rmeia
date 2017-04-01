package com.ism.rmeia.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ism.market.KLine;
public class SimpleCache {

    /**
     * 交易日的cache，用于判断某一天是否是交易日，注，这个是不断递增的cache，因此不会失效
     */
    public Map<String, Float> hs300Caches = new HashMap<>(512);
    
    public Map<String, List<KLine>> klinesMap = new ConcurrentHashMap<>(7200);

    public float getHs300(String yyyyMMdd) {
        Float v = hs300Caches.get(yyyyMMdd);
        if (v == null) {
            v = fetch(yyyyMMdd);
        }
        return v;
    }
    public Float putHs300(String yyyyMMdd, Float value) {
        return hs300Caches.put(yyyyMMdd, value);
    }

    public Map<String, List<KLine>> getKLines() {
        return klinesMap;
    }
    public List<KLine> putKLines(String key, List<KLine> klines) {
        return klinesMap.put(key, klines);
    }

    private float fetch(String yyyyMMdd) {
        return 0;
    }
    public float hs300Profit(int diffOpenDay) {
        return 0;
    }
}
