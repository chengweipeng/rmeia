package com.ism.rmeia.rule.manager;

import com.ism.market.KLine;
import com.ism.rmeia.rule.DefaultCloseDayStockRule;
import com.ism.rmeia.rule.DefaultOpenDayStockRule;
import com.ism.rmeia.rule.DefaultRunDayStockRule;
import com.ism.rmeia.rule.StockRule;


/**
 * Created by wx on 2016/9/23.
 */
public class DefaultKLineRuleManager implements RuleManager<KLine> {


    StockRule<KLine> openDayRule = new DefaultOpenDayStockRule();
    StockRule<KLine> closeDayRule = new DefaultCloseDayStockRule();
    StockRule<KLine> runDayRule =new DefaultRunDayStockRule();


    public DefaultKLineRuleManager(){

    }


    @Override
    public StockRule<KLine> getRule(int diffOpenDay, int diffCloseDay) {
        if(diffOpenDay == 0){
            return openDayRule;
        }
        if(diffCloseDay >= 0){
            return closeDayRule;
        }
        return runDayRule;
    }
}
