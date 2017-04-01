package com.ism.rmeia.rule;

import java.util.List;

/**
 * 单个股票的决定规则
 * @author wx
 */
public interface StockRule<T> {
    /**
     * 
     * @return 1:买入，0:不操作，-1:卖出
     */
    public int judge(List<T> kLines);
}
