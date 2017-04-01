package com.ism.rmeia.rule;

import com.ism.market.KLine;
import com.ism.rmeia.day.strategy.CloseDayMarketParameter;
import com.ism.rmeia.day.strategy.OpenDayMarketParameter;

import java.util.List;

/**
 * Created by wx on 2016/9/23.
 */
public class DefaultCloseDayStockRule implements StockRule<KLine> {

    public DefaultCloseDayStockRule(){

    }
    public int judge(List<KLine> kLines){
        CloseDayMarketParameter param = new CloseDayMarketParameter();
        int k = kLines.size()-1;
        param.close_0 =  kLines.get(k-0).CLOSE;
        param.close_4 =  kLines.get(k-4).CLOSE;

        param.dif_1 =  kLines.get(k-1).DIF;
        param.dif_2 =  kLines.get(k-2).DIF;
        param.dif_3 =  kLines.get(k-3).DIF;
        param.dif_4 =  kLines.get(k-4).DIF;

        param.dea_1 =  kLines.get(k-1).DEA;
        param.dea_2 =  kLines.get(k-2).DEA;
        param.dea_3 =  kLines.get(k-3).DEA;
        param.dea_4 =  kLines.get(k-4).DEA;
        return SellRule5.sell(param)?-1:0;
    }
}
