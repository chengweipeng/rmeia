package com.ism.rmeia.day.strategy;

import static com.ism.rmeia.rule.SoldRule1.getRules;

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
import com.ism.rmeia.rule.SoldRule1;
import com.ism.util.Config;

public class RunDayStrategyTest {

    UserInfo userInfo = new UserInfo();
    Map<String, StockSignal> stockSignalMap;

    @Before
    public void setUp() {
      ArrayList<SoldRule1.RuleValue> l = getRules();
        l.forEach(System.out::println);
    }

    /**
     * T+1 设定条件，当前价格《收盘价格，手头有钱
     * 上涨趋势选择买入
     * V2 调仓不买
     */

    @Test
    public void testProduceDelegateStocks_T1() {
        IABasic basic = new IABasic();
        userInfo.setBasic(basic);
        userInfo.getStocks().clear();

        basic.setDiffOpenDay(1);
        basic.setCloseDay(false);
        basic.setMsgId("");
        basic.setIa_status((byte) 3);
        basic.setIsm_id("testism");
        basic.setUser_id("test_rmeia");
        basic.setOrder_id("unknown");
        basic.setTotalFreeMoney(20000);
        {
            IAStock stock = new IAStock();
            stock.setAvailableNum(500);
            stock.setNeedOpenBuyNum(1000);
            stock.setAvgPrice(4);
            stock.setCurPrice(4);
            stock.setOwnNum(500);
            stock.setStockCode("601398");

            userInfo.getStocks().add(stock);
            stockSignalMap = new HashMap<String, StockSignal>();
            StockSignal sig = new StockSignal();
            sig.lastupdate = (int) (System.currentTimeMillis() / 1000);
            sig.pred_15min = 4.5f;
            sig.pred_close = 5.0f;
            sig.pre_close = 4.0f;
            sig.sig_tm = sig.lastupdate - 10 * 60;
            sig.pred_close_std = 0.72;
            stockSignalMap.put("601398", sig);

        }

        {
            IAStock stock = new IAStock();
            stock.setAvailableNum(500);
            stock.setNeedOpenBuyNum(1000);
            stock.setAvgPrice(4);
            stock.setCurPrice(4);
            stock.setOwnNum(500);
            stock.setStockCode("000001");

            userInfo.getStocks().add(stock);
            stockSignalMap = new HashMap<String, StockSignal>();
            StockSignal sig = new StockSignal();
            sig.lastupdate = (int) (System.currentTimeMillis() / 1000);
            sig.pred_15min = 4.5f;
            sig.pred_close = 5.0f;
            sig.pre_close = 4.0f;
            sig.sig_tm = sig.lastupdate - 10 * 60;
            sig.pred_close_std = 0.0072;
            stockSignalMap.put("000001", sig);
        }

        RunDayStrategy strategy = new RunDayStrategy(userInfo, userInfo.getStocks(), stockSignalMap,null);
        Collection<DelegateStock> stocks = strategy.produceDelegateStocks();
        Assert.assertTrue(stocks.isEmpty());

        stocks.forEach(delegateStock -> {
            Assert.assertTrue(delegateStock.isBuyDir());
            System.out.println(delegateStock);
        });
    }

    /**
     * T+1 设定条件，>Y1选择卖出
     */

    @Test
    public void testProduceDelegateStocks_T1Sell() {
        IABasic basic = new IABasic();
        userInfo.setBasic(basic);
        userInfo.getStocks().clear();

        basic.setDiffOpenDay(1);
        basic.setCloseDay(false);
        basic.setMsgId("");
        basic.setIa_status((byte) 3);
        basic.setIsm_id("testism");
        basic.setUser_id("test_rmeia");
        basic.setOrder_id("unknown");
        basic.setTotalFreeMoney(20000);
        basic.setTotalMoney(200000);
        {
            IAStock stock = new IAStock();
            stock.setAvailableNum(500);
            stock.setNeedOpenBuyNum(1000);
            stock.setAvgPrice(6);
            stock.setCurPrice(10);
            stock.setOwnNum(500);
            stock.setStockCode("601398");

            userInfo.getStocks().add(stock);
            stockSignalMap = new HashMap<String, StockSignal>();
            StockSignal sig = new StockSignal();
            sig.lastupdate = (int) (System.currentTimeMillis() / 1000);
            sig.pred_15min = 4.5f;
            sig.pred_close = 4.0f;
            sig.pre_close = 6.0f;
            sig.sig_tm = sig.lastupdate - 10 * 60;
            sig.pred_close_std = 0.72;
            stockSignalMap.put("601398", sig);
        }
        RunDayStrategy.setY1((float) 0.06);
        RunDayStrategy.setY2((float) 0.03);
        RunDayStrategy.setZ((float) -0.02);
        RunDayStrategy strategy = new RunDayStrategy(userInfo, userInfo.getStocks(), stockSignalMap,null);
        Collection<DelegateStock> stocks = strategy.produceDelegateStocks();
        Assert.assertTrue(!stocks.isEmpty());
        stocks.forEach(delegateStock -> {
            Assert.assertTrue(!delegateStock.isBuyDir());
            System.out.println(delegateStock);
        });
    }

    /**
     * T+3 设定条件，<z选择卖出
     */
    @Test
    public void testProduceDelegateStocks_T3Sell() {
        IABasic basic = new IABasic();
        userInfo.setBasic(basic);
        userInfo.getStocks().clear();

        basic.setDiffOpenDay(3);
        basic.setCloseDay(false);
        basic.setMsgId("");
        basic.setIa_status((byte) 3);
        basic.setIsm_id("testism");
        basic.setUser_id("test_rmeia");
        basic.setOrder_id("unknown");
        basic.setTotalFreeMoney(20000);
        basic.setTotalMoney(200000);
        {
            IAStock stock = new IAStock();
            stock.setAvailableNum(500);
            stock.setNeedOpenBuyNum(1000);
            stock.setAvgPrice(8);
            stock.setCurPrice(5);
            stock.setOwnNum(500);
            stock.setStockCode("601398");

            userInfo.getStocks().add(stock);
            stockSignalMap = new HashMap<String, StockSignal>();
            StockSignal sig = new StockSignal();
            sig.lastupdate = (int) (System.currentTimeMillis() / 1000);
            sig.pred_15min = 6.0f;
            sig.pred_close = 5.0f;
            sig.pre_close = 4.0f;
            sig.sig_tm = sig.lastupdate - 10 * 60;
            sig.pred_close_std = 0.72;
            sig.pred_15min_std = 0.46;
            sig.stock_id = "601398";
            stockSignalMap.put("601398", sig);

        }
        RunDayStrategy.setY1((float) 0.06);
        RunDayStrategy.setY2((float) 0.03);
        RunDayStrategy.setZ((float) -0.02);
        RunDayStrategy strategy = new RunDayStrategy(userInfo, userInfo.getStocks(), stockSignalMap,null);
        Collection<DelegateStock> stocks = strategy.produceDelegateStocks();
        Assert.assertTrue(!stocks.isEmpty());
    }
    @Test
    /**
     * T+1 设定条件， >Y2&&<Y1选择卖出
     */
    public void testProduceDelegateStocks_T1600758Sell() {
        Calendar c = Calendar.getInstance();
        c.set(2015, 12, 22, 10, 0, 0);
        
        IABasic basic = new IABasic();
        userInfo.setBasic(basic);
        userInfo.getStocks().clear();

        basic.setDiffOpenDay(1);
        basic.setCloseDay(false);
        basic.setMsgId("");
        basic.setIa_status((byte) 3);
        basic.setIsm_id("testism");
        basic.setUser_id("test_rmeia");
        basic.setOrder_id("unknown");
        basic.setTotalFreeMoney(0);
        basic.setTotalMoney(98500);
        {
            IAStock stock = new IAStock();
            stock.setAvailableNum(500);
            stock.setNeedOpenBuyNum(0);
            stock.setAvgPrice(13.92);
            stock.setCurPrice(14.42);
            stock.setOwnNum(500);
            stock.setStockCode("600758");

            userInfo.getStocks().add(stock);
            stockSignalMap = new HashMap<String, StockSignal>();
            StockSignal sig = new StockSignal();
            
            sig.lastupdate = (int) (c.getTimeInMillis());
            sig.pred_15min = 13.68f;
            sig.pred_close = 13.76f;
            sig.pre_close = 13.80f;
            sig.sig_tm = sig.lastupdate - 10 * 60;
            sig.pred_close_std = 0.72;
            sig.pred_15min_std = 0.46;
            sig.stock_id = "600758";
            
            stockSignalMap.put(sig.stock_id, sig);
        }
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
        kLines.put("600758", klinelist);
        RunDayStrategy.setY1((float) 0.06);
        RunDayStrategy.setY2((float) 0.03);
        RunDayStrategy.setZ((float) -0.02);
        RunDayStrategy strategy = new RunDayStrategy(userInfo, userInfo.getStocks(), stockSignalMap,kLines);
        strategy.setOperationTime(c.getTimeInMillis());
        
        Collection<DelegateStock> stocks = strategy.produceDelegateStocks();
        Assert.assertTrue(!stocks.isEmpty());

    }
    
    @Test
    public void testRunSell(){
        
        IAStock stock = new IAStock();
        stock.setAvailableNum(500);
        stock.setNeedOpenBuyNum(0);
        stock.setAvgPrice(13.92);
        stock.setCurPrice(14.86);
        stock.setOwnNum(500);
        stock.setStockCode("600758");
            
        RunDayStrategy strategy = new RunDayStrategy(null, null, null,null);

        Config cfg = new Config();
        cfg.put("Rule.Y1",0.05f);
        cfg.put("Rule.Y2",0.03f);
        cfg.put("Rule.Z",-0.02f);
        strategy.configure(cfg);
       
        double curPrice = (float) stock.getCurPrice();
        double avgCost = stock.getAvgPrice();
        float curProfit = (float) ((curPrice - avgCost) / avgCost); 
               
        DelegateStock delegateSellStock = strategy.needSell(stock, curProfit);
        Assert.assertNotNull(delegateSellStock);
        System.out.println(delegateSellStock);
    }
    
    
    
    
}
