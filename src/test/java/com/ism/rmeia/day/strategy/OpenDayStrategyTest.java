package com.ism.rmeia.day.strategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ism.market.KLine;
import com.ism.rmeia.bean.DelegateStock;
import com.ism.rmeia.bean.IABasic;
import com.ism.rmeia.bean.IAStock;
import com.ism.rmeia.bean.StockSignal;
import com.ism.rmeia.bean.UserInfo;

public class OpenDayStrategyTest {

    UserInfo userInfo = new UserInfo();
    Map<String, StockSignal> stockSignalMap;

    @Before
    public void setUp() {

    }

    /**
     * T+0 建仓卖出规则测试
     * v2：14:45之后
     */
    @Test
    public void testProduceDelegateStocks_Tplus0() {
        Calendar c = Calendar.getInstance();
        c.set(2015, 12, 22, 14, 50, 0);
        
        IABasic basic = new IABasic();
        basic.setDiffOpenDay(0);
        basic.setCloseDay(false);
        basic.setMsgId("");
        basic.setIa_status((byte) 3);
        basic.setIsm_id("testism");
        basic.setUser_id("test_rmeia");
        basic.setOrder_id("unknown");
        basic.setTotalFreeMoney(20000);
        IAStock stock = new IAStock();
        stock.setAvailableNum(0);
        stock.setNeedOpenBuyNum(1000);
        stock.setAvgPrice(4);
        stock.setCurPrice(5);
        stock.setOwnNum(500);
        stock.setStockCode("601398");
        userInfo.setBasic(basic);
        userInfo.getStocks().clear();
        userInfo.getStocks().add(stock);
        stockSignalMap = new HashMap<String, StockSignal>();
        StockSignal sig = new StockSignal();
        sig.lastupdate = (int) (System.currentTimeMillis() / 1000);
        sig.pred_15min = 4.5f;
        sig.pred_close = 5.0f;
        sig.pre_close = 4.0f;
        sig.sig_tm = sig.lastupdate - 10 * 60;
        stockSignalMap.put("601398", sig);
               
        Map<String, List<KLine>> kLines = new HashMap<String,List<KLine>>();
        ArrayList<KLine>  klinelist = new ArrayList<>();        
        kLines.put("601398", klinelist);
        
        OpenDayStrategy strategy = new OpenDayStrategy(userInfo, userInfo.getStocks(), stockSignalMap,kLines);
        strategy.setOperationTime(c.getTimeInMillis());
        Collection<DelegateStock> stocks = strategy.produceDelegateStocks();
        Assert.assertTrue(!stocks.isEmpty());
        stocks.forEach(delegateStock -> {
            System.out.println(delegateStock);
        });
    }
    
    /*14:45之前
     * 
     * */
    @Test 
    public void testProduceDelegateStocks_1512244600022Tplus0() {

        IABasic basic = new IABasic();
        basic.setDiffOpenDay(0);
        basic.setCloseDay(false);
        basic.setMsgId("RMEIA.20151228100028.192.168.10.126:5436.42752.4789");
        basic.setIa_status((byte) 3);
        basic.setIsm_id("testism");
        basic.setUser_id("test_rmeia");
        basic.setOrder_id("unknown");
        basic.setTotalFreeMoney(200000);
        IAStock stock = new IAStock();
        stock.setAvailableNum(0);
        stock.setNeedOpenBuyNum(3700);
        stock.setAvgPrice(0);
        stock.setCurPrice(9.30);
        stock.setOwnNum(0);
        stock.setStockCode("000552");
        userInfo.setBasic(basic);
        userInfo.getStocks().clear();
        userInfo.getStocks().add(stock);
        stockSignalMap = new HashMap<String, StockSignal>();
        StockSignal sig = new StockSignal();
        sig.lastupdate = (int) (System.currentTimeMillis() / 1000);
        sig.pred_15min = 4.5f;
        sig.pred_close = 5.0f;
        sig.pre_close = 4.0f;
        sig.sig_tm = sig.lastupdate - 10 * 60;
        sig.vpin = 0.63f;
        sig.impact = (double)0.0003;  
        stockSignalMap.put("000552", sig);

        Map<String, List<KLine>> kLines = new HashMap<String,List<KLine>>();
        ArrayList<KLine>  klinelist = new ArrayList<>();      
        
        KLine kline0 = new KLine();
        kline0.CLOSE = 1;
        kline0.DEA = -1;
        kline0.DIF = -2;
        kline0.MA = 6;
        KLine kline1 = new KLine();
        kline1.CLOSE = 1;
        kline1.DEA = -2;
        kline1.DIF = -1;
        kline1.MA = 1;
        KLine kline2 = new KLine();
        kline2.CLOSE = 1;
        kline2.DEA = 1;
        kline2.DIF = 2;
        kline2.MA = 1;
        KLine kline3 = new KLine();
        kline3.CLOSE = 1;
        kline3.DEA = 1;
        kline3.DIF = 2;
        kline3.MA = 1;
        KLine kline4 = new KLine();
        kline4.CLOSE = 5;
        kline4.DEA = 5;
        kline4.DIF = 10;
        kline4.MA = 1;
        klinelist.add(kline0);
        klinelist.add(kline1);
        klinelist.add(kline2);
        klinelist.add(kline3);
        klinelist.add(kline4);
        kLines.put("000552", klinelist);
        
        OpenDayStrategy strategy = new OpenDayStrategy(userInfo, userInfo.getStocks(), stockSignalMap,kLines);
        Calendar c = Calendar.getInstance();
        c.set(2015, 12, 22, 10, 0, 0);
        strategy.setOperationTime(c.getTimeInMillis());
        Collection<DelegateStock> stocks = strategy.produceDelegateStocks();
        Assert.assertTrue(!stocks.isEmpty());
        stocks.forEach(delegateStock -> {
            Assert.assertEquals(true, delegateStock.isBuyDir());
            Assert.assertEquals(3700,delegateStock.getEntrustNum());
            System.out.println(delegateStock);
        });
    }
    
    
    /**
     * 如果信号超时，那么买入失败
     */
    @Test
    public void testBuyTimeout() {

        IABasic basic = new IABasic();
        basic.setDiffOpenDay(0);
        basic.setCloseDay(false);
        basic.setMsgId("RMEIA.20151228100028.192.168.10.126:5436.42752.4789");
        basic.setIa_status((byte) 3);
        basic.setIsm_id("testism");
        basic.setUser_id("test_rmeia");
        basic.setOrder_id("unknown");
        basic.setTotalFreeMoney(200000);
        IAStock stock = new IAStock();
        stock.setAvailableNum(0);
        stock.setNeedOpenBuyNum(3700);
        stock.setAvgPrice(0);
        stock.setCurPrice(9.30);
        stock.setOwnNum(0);
        stock.setStockCode("000552");
        userInfo.setBasic(basic);
        userInfo.getStocks().clear();
        userInfo.getStocks().add(stock);
        stockSignalMap = new HashMap<String, StockSignal>();
        StockSignal sig = new StockSignal();
        sig.lastupdate = (int) (System.currentTimeMillis() / 1000);
        sig.pred_15min = 4.5f;
        sig.pred_close = 5.0f;
        sig.pre_close = 4.0f;
        sig.sig_tm = sig.lastupdate - 20 * 60;//信号过期了
        stockSignalMap.put("000552", sig);

        Map<String, List<KLine>> kLines = new HashMap<String,List<KLine>>();
        ArrayList<KLine>  klinelist = new ArrayList<>();      
        
        KLine kline0 = new KLine();
        kline0.ADJUST = 1;
        kline0.CLOSE = 1;
        kline0.DEA = -1;
        kline0.DIF = -2;
        kline0.EMA12 = 0;
        kline0.EMA26 = 0;
        kline0.MA = 6;
        kline0.ts = (int) System.currentTimeMillis();
        KLine kline1 = new KLine();
        kline1.ADJUST = 1;
        kline1.CLOSE = 1;
        kline1.DEA = -2;
        kline1.DIF = -1;
        kline1.EMA12 = 0;
        kline1.EMA26 = 0;
        kline1.MA = 1;
        kline1.ts = (int) System.currentTimeMillis();
        KLine kline2 = new KLine();
        kline2.ADJUST = 10;
        kline2.CLOSE = 1;
        kline2.DEA = 1;
        kline2.DIF = 2;
        kline2.EMA12 = 0;
        kline2.EMA26 = 0;
        kline2.MA = 1;
        kline2.ts = (int) System.currentTimeMillis();
        KLine kline3 = new KLine();
        kline3.ADJUST = 10;
        kline3.CLOSE = 1;
        kline3.DEA = 1;
        kline3.DIF = 2;
        kline3.EMA12 = 0;
        kline3.EMA26 = 0;
        kline3.MA = 1;
        kline3.ts = (int) System.currentTimeMillis();
        KLine kline4 = new KLine();
        kline4.ADJUST = 10;
        kline4.CLOSE = 5;
        kline4.DEA = 5;
        kline4.DIF = 10;
        kline4.EMA12 = 0;
        kline4.EMA26 = 0;
        kline4.MA = 1;
        kline4.ts = (int) System.currentTimeMillis();
        klinelist.add(kline0);
        klinelist.add(kline1);
        klinelist.add(kline2);
        klinelist.add(kline3);
        klinelist.add(kline4);
        kLines.put("000552", klinelist);
        
        OpenDayStrategy strategy = new OpenDayStrategy(userInfo, userInfo.getStocks(), stockSignalMap,kLines);
        Calendar c = Calendar.getInstance();
        c.set(2015, 12, 22, 10, 0, 0);
        strategy.setOperationTime(c.getTimeInMillis());
        Collection<DelegateStock> stocks = strategy.produceDelegateStocks();
        Assert.assertTrue(!stocks.isEmpty());
    }
    @Test
    public void testTimeout(){
        StockSignal sig = new StockSignal();
        sig.lastupdate = (int) (System.currentTimeMillis() / 1000);
        sig.pred_15min = 4.5f;
        sig.pred_close = 5.0f;
        sig.pre_close = 4.0f;
        sig.sig_tm = sig.lastupdate - 20 * 60;//信号过期了
        OpenDayStrategy strategy =new OpenDayStrategy(null, null, null,null);
        Assert.assertTrue(strategy.isTimeout(sig.sig_tm));
    }
    @Test
    public void testBuyFee(){
        IABasic basic = new IABasic();
        basic.setDiffOpenDay(0);
        basic.setCloseDay(false);
        basic.setMsgId("RMEIA.20151228100028.192.168.10.126:5436.42752.4789");
        basic.setIa_status((byte) 3);
        basic.setIsm_id("testism");
        basic.setUser_id("test_rmeia");
        basic.setOrder_id("unknown");
        basic.setTotalFreeMoney(200000);
        IAStock stock = new IAStock();
        stock.setAvailableNum(0);
        stock.setNeedOpenBuyNum(3700);
        stock.setAvgPrice(0);
        stock.setCurPrice(9.30);
        stock.setOwnNum(0);
        stock.setStockCode("000552");
        userInfo.setBasic(basic);
        userInfo.getStocks().clear();
        userInfo.getStocks().add(stock);
        stockSignalMap = new HashMap<String, StockSignal>();
        StockSignal sig = new StockSignal();
        sig.lastupdate = (int) (System.currentTimeMillis() / 1000);
        sig.pred_15min = 4.5f;
        sig.pred_close = 5.0f;
        sig.pre_close = 4.0f;
        sig.sig_tm = sig.lastupdate - 10 * 60;
        stockSignalMap.put("000552", sig);

        Map<String, List<KLine>> kLines = new HashMap<String,List<KLine>>();
        ArrayList<KLine>  klinelist = new ArrayList<>();        
        for (int i = 0; i < 5; i++) {
            KLine kline0 = new KLine();
            kline0.ADJUST = 10;
            kline0.CLOSE = 10;
            kline0.DEA = 0;
            kline0.DIF = 10;
            kline0.EMA12 = 0;
            kline0.EMA26 = 0;
            kline0.MA = 10;
            kline0.ts = (int) System.currentTimeMillis();
            klinelist.add(kline0);
        }        
        kLines.put("000552", klinelist);
        
        OpenDayStrategy strategy = new OpenDayStrategy(userInfo, userInfo.getStocks(), stockSignalMap,kLines);
        Calendar c = Calendar.getInstance();
        c.set(2015, 12, 22, 10, 0, 0);
        strategy.setOperationTime(c.getTimeInMillis());
        Collection<DelegateStock> stocks = strategy.produceDelegateStocks();
        Assert.assertTrue(stocks.isEmpty());
        stocks.forEach(delegateStock -> {
            Assert.assertEquals(true, delegateStock.isBuyDir());
            Assert.assertEquals(3700,delegateStock.getEntrustNum());
            Assert.assertEquals(new BigDecimal(4.75), new BigDecimal(delegateStock.getEntrustPrice()));
            System.out.println(delegateStock);
        });
    }
}
