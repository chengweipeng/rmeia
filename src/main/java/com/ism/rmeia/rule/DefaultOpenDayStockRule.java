package com.ism.rmeia.rule;

import com.ism.market.KLine;
import com.ism.rmeia.day.strategy.OpenDayMarketParameter;
import com.ism.rmeia.day.strategy.OpenDayStrategy;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wx on 2016/9/23.
 */
public class DefaultOpenDayStockRule implements StockRule<KLine> {
    private final static Logger logger = LoggerFactory.getLogger(DefaultOpenDayStockRule.class);
    public DefaultOpenDayStockRule(){

    }

    public int judge(List<KLine> kLines){
        OpenDayMarketParameter param = new OpenDayMarketParameter();
        int k = kLines.size()-1;
        param.ma_4 = kLines.get(k-4).MA;
        
        param.close_0 =  kLines.get(k-0).CLOSE;
        param.close_1 =  kLines.get(k-1).CLOSE;
        param.close_2 =  kLines.get(k-2).CLOSE;
        param.close_3 =  kLines.get(k-3).CLOSE;
        param.close_4 =  kLines.get(k-4).CLOSE;

        param.dif_0 =  kLines.get(k-0).DIF;
        param.dif_1 =  kLines.get(k-1).DIF;
        param.dif_2 =  kLines.get(k-2).DIF;
        param.dif_3 =  kLines.get(k-3).DIF;
        param.dif_4 =  kLines.get(k-4).DIF;

        param.dea_0 =  kLines.get(k-0).DEA;
        param.dea_1 =  kLines.get(k-1).DEA;
        param.dea_2 =  kLines.get(k-2).DEA;
        param.dea_3 =  kLines.get(k-3).DEA;
        param.dea_4 =  kLines.get(k-4).DEA;
        //logger.info("Open:buyRule.kLines.close4={},ma4={},tms4={}",param.close_4,param.ma_4,kLines.get(k-4).ts);
        return BuyRule4.buy(param)?1:0;
    }
}
