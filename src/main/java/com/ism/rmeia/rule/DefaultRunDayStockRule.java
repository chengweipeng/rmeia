package com.ism.rmeia.rule;

import com.ism.market.KLine;
import com.ism.rmeia.rule.StockRule;

import java.util.List;

/**
 * Created by wx on 2016/9/23.
 */
public class DefaultRunDayStockRule implements StockRule<KLine> {

    public DefaultRunDayStockRule(){
    }

    @Override
    public int judge(List<KLine> kLines) {
        return 0;
    }
}
