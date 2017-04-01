package com.ism.rmeia.query;

import java.util.HashMap;
import java.util.Map;

import com.ism.rmeia.bean.PredStock;

public class PredictStockQuery {

    /**
     * 
     * @param predStockMap
     * @param stockCode
     * @return
     */
    public Map<String, PredStock> getPredictStock(Map<String, PredStock> predStockMap, String stockCode) {

        Map<String, PredStock> retMap = new HashMap<>();

        PredStock predStock = retMap.get(stockCode);
        if (predStock == null) {
            predStock = new PredStock();
        }
        retMap.put(stockCode, predStock);

        return retMap;
    }

    /**
     * @param predStockMap
     * @param stocks
     * @return
     */
    public Map<String, PredStock> getPredictStock(Map<String, PredStock> predStockMap, String... stocks) {
        if (stocks == null || predStockMap == null) {
            return new HashMap<>();
        }
        Map<String, PredStock> retMap = new HashMap<>();
        for (String stockCode : stocks) {
            PredStock predStock = retMap.get(stockCode);
            if (predStock == null) {
                predStock = new PredStock();
            }
            retMap.put(stockCode, predStock);
        }
        return retMap;
    }
}
