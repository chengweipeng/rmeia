package com.ism.rmeia.day.strategy;

import com.ism.market.KLine;
import com.ism.market.hbase.KLineGetUtil;
import com.ism.rmeia.bean.IAStock;
import com.ism.rmeia.bean.StockSignal;
import com.ism.rmeia.bean.UserInfo;
import com.ism.rmeia.dto.StockSignalDto;
import com.ism.rmeia.util.Enviroment;
import com.ism.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class AbstractStrategy  implements DayStockStrategy{
    private final static Logger logger = LoggerFactory.getLogger(AbstractStrategy.class);
    final Collection<IAStock> stocks;
    final Map<String, StockSignal> sigMap;
    final Map<String, List<KLine>> kLinesMap;
    final UserInfo userInfo;
    private long operationTime;// 操作时间,单位ms

    static boolean debugDetail = false;
    public AbstractStrategy(final UserInfo userInfo, final Collection<IAStock> stocks, final Map<String, StockSignal> sigMap,final Map<String, List<KLine>> kLines) {
        this.stocks = stocks;
        this.sigMap = sigMap;
        this.userInfo = userInfo;
        this.kLinesMap = kLines;
        setOperationTime(System.currentTimeMillis());
    }
    /**
     * 
     * @return 操作时间
     */
    public long getOperationTime() {
        return operationTime;
    }

    /**
     * 设置平仓操作时间
     * 
     * @param operationTime
     */
    // @VisibleForTesting
    public void setOperationTime(long operationTime) {
        this.operationTime = operationTime;
    }
    /**
     * 判断信号的时间是否超时
     * @param sigTimeInSec
     * @return true if timeout, otherwise false;
     */
    public boolean isTimeout(int sigTimeInSec){
        if(Enviroment.devMode) {
            return false;
        }
        else {
            final int diff = (int) (getOperationTime() / 1000 - sigTimeInSec);
            return diff >= StockSignalDto.SLICE_IN_SECONDS + 2 * 60;
        }
    }

    public static OpenDayMarketParameter genOpenDayParameter(final Map<String, List<KLine>> kLinesMap,String stockCode){
        
        List<KLine> klines = KLineGetUtil.getSeq(kLinesMap, stockCode);
        if(klines.size() <= 5 ){
            throw new RuntimeException("K lines loadDefaultStrategy error.");
        }
        OpenDayMarketParameter param = new OpenDayMarketParameter();
        int k = klines.size()-1;
        param.close_0 =  klines.get(k-0).CLOSE;
        param.close_1 =  klines.get(k-1).CLOSE;
        param.close_2 =  klines.get(k-2).CLOSE;
        param.close_3 =  klines.get(k-3).CLOSE;
        param.close_4 =  klines.get(k-4).CLOSE;
        
        param.dif_1 =  klines.get(k-1).DIF;
        param.dif_2 =  klines.get(k-2).DIF;
        param.dif_3 =  klines.get(k-3).DIF;
        param.dif_4 =  klines.get(k-4).DIF;
        
        param.dea_1 =  klines.get(k-1).DEA;
        param.dea_2 =  klines.get(k-2).DEA;
        param.dea_3 =  klines.get(k-3).DEA;
        param.dea_4 =  klines.get(k-4).DEA;

        if(debugDetail) {
            logger.debug("Parameter={}", param);
        }
        return param;
    }
    
    public static RunDayMarketParameter genRunDayParameter(final Map<String, List<KLine>> kLinesMap,String stockCode){
        
        RunDayMarketParameter param = new RunDayMarketParameter();
        List<KLine> klines = KLineGetUtil.getSeq(kLinesMap, stockCode);
        if(klines.size() < 5 ){
            throw new RuntimeException("K lines loadDefaultStrategy error.");
        }
        int k = klines.size()-1;
        param.ma_4 = klines.get(k-4).MA;
        
        param.close_0 =  klines.get(k-0).CLOSE;
        param.close_1 =  klines.get(k-1).CLOSE;
        param.close_2 =  klines.get(k-2).CLOSE;
        param.close_3 =  klines.get(k-3).CLOSE;
        param.close_4 =  klines.get(k-4).CLOSE;

        param.dif_0 =  klines.get(k-0).DIF;
        param.dif_1 =  klines.get(k-1).DIF;
        param.dif_2 =  klines.get(k-2).DIF;
        param.dif_3 =  klines.get(k-3).DIF;
        param.dif_4 =  klines.get(k-4).DIF;
        
        param.dea_0 =  klines.get(k-0).DEA;
        param.dea_1 =  klines.get(k-1).DEA;
        param.dea_2 =  klines.get(k-2).DEA;
        param.dea_3 =  klines.get(k-3).DEA;
        param.dea_4 =  klines.get(k-4).DEA;
        if(debugDetail) {
            logger.debug("Parameter={}", param);
        }
        return param;
    }
    
    
    public static CloseDayMarketParameter genCloseDayParameter(final Map<String, List<KLine>> kLinesMap,String stockCode){
        
        CloseDayMarketParameter param = new CloseDayMarketParameter();
        List<KLine> klines = KLineGetUtil.getSeq(kLinesMap, stockCode);
        if(klines.size() < 5 ){
            throw new RuntimeException("K lines loadDefaultStrategy error.");
        }
        int k = klines.size()-1;
        param.ma_4 = klines.get(k-4).MA;
        
        param.close_0 =  klines.get(k-0).CLOSE;
        param.close_1 =  klines.get(k-1).CLOSE;
        param.close_2 =  klines.get(k-2).CLOSE;
        param.close_3 =  klines.get(k-3).CLOSE;
        param.close_4 =  klines.get(k-4).CLOSE;

        param.dif_0 =  klines.get(k-0).DIF;
        param.dif_1 =  klines.get(k-1).DIF;
        param.dif_2 =  klines.get(k-2).DIF;
        param.dif_3 =  klines.get(k-3).DIF;
        param.dif_4 =  klines.get(k-4).DIF;
        
        param.dea_0 =  klines.get(k-0).DEA;
        param.dea_1 =  klines.get(k-1).DEA;
        param.dea_2 =  klines.get(k-2).DEA;
        param.dea_3 =  klines.get(k-3).DEA;
        param.dea_4 =  klines.get(k-4).DEA;
        if(debugDetail) {
            logger.debug("Parameter={}", param);
        }
        return param;
    }
    
    /**0
     * @param timeInMs 时刻
     * @param hour 小时，HH
     * @param min 分钟
     * @return 是否晚于北京时间
     */
    public static boolean after(long timeInMs,int hour,int min) {
        return (timeInMs/1000 + 8*60*60) %86400  > (hour*60+min)*60;
    }
    public static void configure(Config cfg){
        debugDetail = cfg.getBoolean("strategy.debug",false);
    }
}
