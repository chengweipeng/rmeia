package com.ism.rmeia.day.strategy;

import com.ism.market.KLine;
/**
 * Created by wx on 2015/11/29.
 * Copyright https://www.zipeiyi.com
 * All rights reserved.
 */
import com.ism.rmeia.bean.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;


public class CloseDayStrategyTest {

    UserInfo userInfo = new UserInfo();
    Map<String, StockSignal> stockSignalMap;

    @Before
    public void setUp() {

    }

    /**
     * 平仓卖出规则测试 上午14：45之后
     */
    @Test
    public void testProduceDelegateStocks_beforeMiddle() {

        IABasic basic = new IABasic();
        basic.setDiffOpenDay(7);
        basic.setCloseDay(true);
        basic.setMsgId("");
        basic.setIa_status((byte) 3);
        basic.setIsm_id("testism");
        basic.setUser_id("test_rmeia");
        basic.setOrder_id("unknown");
        basic.setTotalFreeMoney(20000);
        IAStock stock = new IAStock();
        stock.setAvailableNum(1000);
        stock.setNeedOpenBuyNum(1000);
        stock.setAvgPrice(4);
        stock.setCurPrice(5);
        stock.setOwnNum(1000);
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
        
        KLine kline0 = new KLine();      
        kline0.CLOSE = 10;
        kline0.DEA = 0 ;
        kline0.DIF = 10;
        kline0.MA = 10;    
        klinelist.add(kline0);
        kLines.put("601398", klinelist);
        
        CloseDayStrategy strategy = new CloseDayStrategy(userInfo, userInfo.getStocks(), stockSignalMap,kLines);
        Calendar c = Calendar.getInstance(Locale.CHINA);
        c.set(Calendar.HOUR_OF_DAY,14);
        c.set(Calendar.MINUTE,46);
        strategy.setOperationTime(c.getTimeInMillis());

        Collection<DelegateStock> stocks ;
        stocks = strategy.produceDelegateStocks();
        Assert.assertTrue(!stocks.isEmpty());
        stocks.forEach(delegateStock -> {
            System.out.println(delegateStock);
        });
    }

    /**
     * 平仓卖出规则测试 下午14:45之前
     */
    @Test
    public void testProduceDelegateStocks_afterMiddle() {

        final int AVAILABLE_NUM = 1000;
        IABasic basic = new IABasic();
        basic.setDiffOpenDay(7);
        basic.setCloseDay(false);
        basic.setMsgId("");
        basic.setIa_status((byte) 3);
        basic.setIsm_id("test_ism");
        basic.setUser_id("test_rmeia");
        basic.setOrder_id("unknown");
        basic.setTotalFreeMoney(20000);
        IAStock stock = new IAStock();
        stock.setAvailableNum(AVAILABLE_NUM);
        stock.setNeedOpenBuyNum(1000);
        stock.setAvgPrice(4);
        stock.setCurPrice(5);
        stock.setOwnNum(1000);
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
        KLine kline4 = new KLine();
        kline4.CLOSE = 6;
        kline4.DEA = 1;
        kline4.DIF = 2;
        kline4.MA = 1;
        KLine kline3 = new KLine();       
        kline3.CLOSE = 4;
        kline3.DEA = 2;
        kline3.DIF = 1;        
        kline3.MA = 1;
        KLine kline2 = new KLine();
        kline2.CLOSE = 3;
        kline2.DEA = 2;
        kline2.DIF = 1;
        kline2.MA = 1;
        KLine kline1 = new KLine();
        kline1.CLOSE = 2;
        kline1.DEA = 2;
        kline1.DIF = 1;
        kline1.MA = 1;
        KLine kline0 = new KLine();
        kline0.CLOSE = 1;
        kline0.DEA = 5;
        kline0.DIF = 3;
        kline0.MA = 1;
        klinelist.add(kline4);
        klinelist.add(kline3);
        klinelist.add(kline2);
        klinelist.add(kline1);
        klinelist.add(kline0);
        kLines.put("601398", klinelist);
         
        CloseDayStrategy strategy = new CloseDayStrategy(userInfo, userInfo.getStocks(), stockSignalMap,kLines);
        Calendar c = Calendar.getInstance(Locale.CHINA);
        c.set(Calendar.HOUR_OF_DAY,10);
        strategy.setOperationTime(c.getTimeInMillis());
        Collection<DelegateStock> stocks ;
        stocks = strategy.produceDelegateStocks();
        Assert.assertTrue(!stocks.isEmpty());
        stocks.forEach(delegateStock -> {
            Assert.assertEquals(delegateStock.getEntrustNum(),AVAILABLE_NUM);
            System.out.println(delegateStock);
        });
    }
}
