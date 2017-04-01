package com.ism.rmeia.day.strategy;

import java.util.List;

import com.ism.rmeia.bean.DelegateStock;

public interface DayStockStrategy {
    /**
     * 
     * @return 操作集合，每个股票的操作集合
     */
    public List<DelegateStock> produceDelegateStocks();
}
