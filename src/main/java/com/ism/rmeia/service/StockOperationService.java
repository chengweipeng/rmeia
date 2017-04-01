package com.ism.rmeia.service;

import java.util.Map;

import com.ism.rmeia.bean.StockSignal;
import com.ism.rmeia.bean.UserStockInstruction;

public interface StockOperationService {
    /**
     * 
     * @param signalMap 
     * @param priceMap
     * @param stocks
     * @return &lt;股票代码，操作详情 &gt;
     * @exception NullPointerException if signalMap or priceMap,or stocks is null.
     */
    public Map<String,UserStockInstruction> getStockOperation(Map<String, StockSignal> signalMap, Map<String, Integer> priceMap,String ...stocks);
}
