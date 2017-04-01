package com.ism.market.hbase;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.ism.market.KLine;

/**
 * @author wx
 *         获取股票代码
 */
public class KLineGetUtil{
    static Set<String> stockCodePool;//当日股票池
    static Map<String, List<KLine>> kLineMap;
    
    
    public KLineGetUtil(){
    }
    
    
    /**
     * @param X 参数X
     * @throws IOException
     */
    public static Map<String, List<KLine>> extractKLinesFromHBase(int X) throws IOException {
        Map<String, List<KLine>> kLineMap = getAll();
        init(kLineMap, X);
        calcAllIndexes(kLineMap,X);
        KLineGetUtil.kLineMap  = kLineMap;
        return kLineMap;
    }
    
    /**
     * 返回 HBase存储的对象
     *
     * @return json
     */

    public static Map<String, List<KLine>> getAll() throws IOException {
        KLine1minHbaseTable kLine1minHbaseTable = KLine1minHbaseTable.me;
        KLine1minGetConsumer consumer = new KLine1minGetConsumer();
        kLine1minHbaseTable.getAllRecord(consumer);
        return consumer.getKLineMap();
    }


    /**
     *初始化计算前X个值，如果以后hbase中已经计算好了，那么这个步骤可以忽略
     *
     * 对于MA参数，需要先初始化前X个值，其他参数，初始化1个即可
     * @param kLineMap [IN&OUT] ,传入参数会值被修改
     * @return
     */
    public static Map<String, List<KLine>> init(Map<String, List<KLine>> kLineMap,int X) {

        for(Map.Entry<String, List<KLine>>entry:kLineMap.entrySet()) {
            updateInitParamters(entry.getValue(),X);
        }
        return kLineMap;
    }

    /**
     * 更新初始值
     * @param klines
     */
    @VisibleForTesting
    public static void updateInitParamters(List<KLine> klines,int X) {
        float lastEMA12 = klines.get(0).CLOSE;
        float lastEMA26 = klines.get(0).CLOSE;
        float dif = lastEMA12 - lastEMA26;
        float lastDEA = dif;

        klines.get(0).EMA12 = lastEMA12;
        klines.get(0).EMA26 = lastEMA26;
        klines.get(0).DIF = dif;
        klines.get(0).DEA = lastDEA;
        int k = 0;
        double sum = 0;
        for (k = 0; k < Math.min(klines.size(), X); k++) {
            sum += klines.get(k).CLOSE;
            klines.get(k).MA = (float)(sum/(k+1));
        }
    }

    /**
     *计算KLine各个参数
     * @param kLineMap [IN&OUT]会修改传入的参数
     * @return 修改后的kLineMap
     */
    public static Map<String, List<KLine>> calcAllIndexes(Map<String, List<KLine>> kLineMap,int X) {
        //float ema12_1 = (float) close[last-1];//?这个是这么写么？
        //float ema12_0 = ema12_1*11/13+(float)close[last];

        //计算EMA12
        kLineMap.forEach((stock, klines) -> {
            calcAndUpdateParameters(klines,X);
        });
        return kLineMap;
    }

    /**
     * 前置条件，已经初始化Kline（即前面X条数据已经计算过）
     * @param klines
     * @param X
     */
   public static void calcAndUpdateParameters(List<KLine> klines,int X) {
        double lastEMA12 = klines.get(0).EMA12;
        double lastEMA26 = klines.get(0).EMA26;
        double lastDEA = klines.get(0).DEA;

        for (int i = 1; i < klines.size(); i++) {
            double ema12 = lastEMA12 * 11 / 13 + klines.get(i).CLOSE * 2 / 13;
            double ema26 = lastEMA12 * 25 / 27 + klines.get(i).CLOSE * 2 / 27;
            double dif = ema12 - ema26;
            double dea = lastDEA*8/10+dif*2/10;

            lastEMA12 = ema12;
            lastEMA26=ema26;
            lastDEA = dea;

            klines.get(i).DIF = (float)dif;
            klines.get(i).EMA26 = (float)ema26;
            klines.get(i).EMA12 = (float)ema12;
            klines.get(i).DEA = (float)dea;
        }
        int k = 0;
        double sum = 0;
        for (k = X; k  < klines.size(); k++) {
            sum = klines.get(k).CLOSE + klines.get(k-1).MA*X-klines.get(k-X).CLOSE;
            klines.get(k).MA = (float)(sum/(X));
        }
    }


    public static Map<String, List<KLine>> getAllFrom(int startTimeInSeconds) {
        throw new UnsupportedOperationException("not suppoerted.");
    }
   
    /**
     * 返回K线参数
     * @param stockCode 股票代码,6位，如000001
     * @param lastK 获取时间逆序，第K个，序号从0开始，即倒数第一名，传入<em>lastK=0</em>
     * @return KLine
     */
    public static KLine get(Map<String, List<KLine>> kLineMap, String stockCode,int lastK){
        List<KLine> kLines = kLineMap.get(stockCode);
        if(kLines == null ){
            return null;
        }
        
        int sz = kLines.size();
        if(sz <= lastK){
            return kLines.get(0);
        }
        return kLines.get(sz-lastK-1);
    }
    
    /**
     * 返回K线参数
     * @param kLineMap
     * @param stockCode 股票代码,6位，如000001
     * @return KLine
     */
    public static List<KLine> getSeq(Map<String, List<KLine>> kLineMap, String stockCode){
        List<KLine> kLines = kLineMap.get(stockCode);
        return kLines;
    }

    public static Map<String,List<KLine>> getKlineMap() {
        return kLineMap;
    }
}
