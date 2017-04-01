package com.ism.rmeia.rule.manager;

import com.ism.rmeia.rule.StockRule;

/**
 * Created by wx on 2016/9/23.
 */
public interface RuleManager<T> {

    public StockRule<T> getRule(int diffOpenDay, int diffCloseDay);
}
