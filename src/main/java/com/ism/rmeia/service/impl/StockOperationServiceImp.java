package com.ism.rmeia.service.impl;

import java.util.Map;

import com.ism.rmeia.bean.StockSignal;
import com.ism.rmeia.bean.UserStockInstruction;
import com.ism.rmeia.query.AskStockOperationQuery;
import com.ism.rmeia.service.StockOperationService;

/**
 * 开始支持用户自定义的ISM（即使用类似IA发送数据的格式），rmeia按照调仓逻辑（T+1)，生成买卖指令
 * 
 * @author wx
 * @since 1.1
 */
public class StockOperationServiceImp implements StockOperationService {

    AskStockOperationQuery query;

    public StockOperationServiceImp() {
        this(new AskStockOperationQuery());
    }

    public StockOperationServiceImp(AskStockOperationQuery query) {
        this.query = query;
    }

    /**
     * 
     * @param signalMap 
     * @param priceMap
     * @param stocks
     * @return &lt;股票代码，操作详情 &gt;
     * @exception NullPointerException if signalMap or priceMap,or stocks is null.
     */
    @Override
    public Map<String, UserStockInstruction> getStockOperation(Map<String, StockSignal> signalMap,
            Map<String, Integer> priceMap, String... stocks) {
        if(signalMap ==null || priceMap ==null ||stocks == null){
            throw new NullPointerException("can not be null.");
        }
        return query.getStockOperation(signalMap, priceMap, stocks);
    }
}
