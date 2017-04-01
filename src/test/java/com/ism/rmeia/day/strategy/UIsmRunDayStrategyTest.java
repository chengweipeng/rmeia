package com.ism.rmeia.day.strategy;

import static com.ism.rmeia.rule.SoldRule1.getRules;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.ism.rmeia.bean.IABasic;
import com.ism.rmeia.bean.IAStock;
import com.ism.rmeia.bean.StockSignal;
import com.ism.rmeia.bean.UserInfo;
import com.ism.rmeia.rule.SoldRule1;

import junit.framework.Assert;

public class UIsmRunDayStrategyTest {
    UserInfo userInfo = new UserInfo();
    Map<String, StockSignal> stockSignalMap;

    @Before
    public void setUp() {
      ArrayList<SoldRule1.RuleValue> l = getRules();
        l.forEach(System.out::println);
    }
    
       
    @Test
    public void testProduceDelegateStocks() {
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
            stock.setCurPrice(13.86);
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
        UIsmRunDayStrategy strategy = new UIsmRunDayStrategy(userInfo, userInfo.getStocks(), stockSignalMap,null);
        Assert.assertTrue("UISM  adjust not supported ",strategy.produceDelegateStocks().isEmpty());
  
    }
}
