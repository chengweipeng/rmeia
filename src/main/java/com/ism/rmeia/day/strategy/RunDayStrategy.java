package com.ism.rmeia.day.strategy;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ism.market.KLine;
import com.ism.rmeia.bean.DelegateStock;
import com.ism.rmeia.bean.IABasic;
import com.ism.rmeia.bean.IAStock;
import com.ism.rmeia.bean.StockSignal;
import com.ism.rmeia.bean.UserInfo;
import com.ism.rmeia.cache.loader.SimpleCacheLoader;
import com.ism.rmeia.enumeration.TradingDayType;
import com.ism.rmeia.rule.SellRule5;
import com.ism.util.Config;

/**
 * 调仓日相关规则
 *
 * @author wx
 */
public class RunDayStrategy extends AbstractStrategy {
    private final static Logger logger = LoggerFactory.getLogger(RunDayStrategy.class);
    public static TradingDayType dayType = TradingDayType.R_DAY;
    static float Y1;
    static float Y2;
    static float Z;

    public RunDayStrategy(UserInfo userInfo, Collection<IAStock> stocks, Map<String, StockSignal> sigMap,
            final Map<String, List<KLine>> kLinesMap) {
        super(userInfo, stocks, sigMap, kLinesMap);
    }

    @Override
    public List<DelegateStock> produceDelegateStocks() {

        Date today = new Date(getOperationTime());
        DateFormat sdf = new SimpleDateFormat(SimpleCacheLoader.yyyyMMdd_FORMAT);
        sdf.format(today);
        
        logger.debug("run day.{}", userInfo);
        String ism_id = this.userInfo.getBasic().getIsm_id(); 
        List<IAStock> stocks = this.userInfo.getStocks();
        String userid = this.userInfo.getBasic().getUser_id();
        logger.info("run day.ism_id={},userid={},y1={},y2={},Z={}", ism_id, userid, Y1, Y2, Z);
        // 1.先处理第一层逻辑，即用户整体级别
        /*float profit = getIAProfit(this.userInfo);
        if(debugDetail) {
            logger.info("run day.profit={},ism_id={},y1={},y2={},Z={}", profit, ism_id, Y1, Y2, Z);
        }*/
//        //止盈：大于y1市价平仓
//        if (profit > Y1) {
//            return soldout(userInfo.getStocks());
//        }
//        
//        //止盈：大于y1，小于y2，且满足规则5，市价卖出
//        
//        if(profit > Y2 && profit < Y1){
//            return RundaySold(userInfo.getStocks());
//        }
//         //止损：小于Z(负数)市价平仓
//        if(profit < Z){
//            return soldout(userInfo.getStocks());
//        }
        
        List<DelegateStock> l = new ArrayList<>();
        for (IAStock stock : stocks) {
            if(stock.getAvailableNum() == 0){
                continue;
            }
            float curProfit = getStockProfit(stock);
            DelegateStock delegateSellStock = needSell(stock, curProfit);
           if(delegateSellStock !=null){
               l.add(delegateSellStock);
           }
        
        }
        if(l==null||l.isEmpty())
            return Collections.emptyList();
        else
            return l;
    }

    public DelegateStock needSell(IAStock stock, float curProfit) {
        logger.debug("run day.profit={},stock={},y1={},y2={},Z={}", curProfit, stock.getStockCode(), Y1, Y2, Z);
        if(curProfit > Y1){
            if (stock.getAvailableNum() > 0){
                DelegateStock delegateStock = new DelegateStock(stock);
                delegateStock.setDelegateMethod(1);// 市价委托
                delegateStock.setBuyDir(false);
                delegateStock.setEntrustNum(stock.getAvailableNum());
                delegateStock.setEntrustPrice(stock.getCurPrice());
                return delegateStock;
            } 
        }
        if(curProfit > Y2 && curProfit < Y1){
            if (stock.getAvailableNum() > 0) {
                DelegateStock delegateStock = sellRul5(stock);
                if(delegateStock != null){
                   return delegateStock;
                }
            }
        }
        
        if(curProfit < Z){
            if (stock.getAvailableNum() > 0){
                DelegateStock delegateStock = new DelegateStock(stock);
                delegateStock.setDelegateMethod(1);// 市价委托
                delegateStock.setBuyDir(false);
                delegateStock.setEntrustNum(stock.getAvailableNum());
                delegateStock.setEntrustPrice(stock.getCurPrice());
                return delegateStock;
            } 
        }
        return null;
    }

    private List<DelegateStock> soldout(List<IAStock> stocks) {
        List<DelegateStock> l = new ArrayList<>();
        for (IAStock stock : stocks) {
            if (stock.getAvailableNum() > 0){
                DelegateStock delegateStock = new DelegateStock(stock);
                delegateStock.setDelegateMethod(1);// 市价委托
                delegateStock.setBuyDir(false);
                delegateStock.setEntrustNum(stock.getAvailableNum());
                delegateStock.setEntrustPrice(stock.getCurPrice());
                l.add(delegateStock);
            }
            
        }
        return l;
    }
    
    
    private List<DelegateStock> RundaySold(List<IAStock> stocks){
        List<DelegateStock> sold = new ArrayList<>();
        for (IAStock iaStock : stocks) {
            if (iaStock.getAvailableNum() > 0) {
                DelegateStock delegateStock = sellRul5(iaStock);
                if(delegateStock != null){
                    sold.add(delegateStock);
                }
            }
        }
        return sold;
    }
    
    
    public DelegateStock sellRul5(IAStock iaStock){
        //double curPrice = iaStock.getCurPrice();
        List<KLine> kLines = kLinesMap.get(iaStock.getStockCode());
        if (kLines != null && kLines.size() < 5) {
            logger.warn("[Run:SellRule] k lines is illegal,stockcode={},kLINE.SIZE={}",iaStock.getStockCode(),kLines.size());
            return null;
        }
        String stockCode = iaStock.getStockCode();
        RunDayMarketParameter param = genRunDayParameter(this.kLinesMap,stockCode);
        boolean sell = SellRule5.sell(param);
        if(sell){
            DelegateStock delegateStock  =  new DelegateStock(iaStock);
            delegateStock.setBuyDir(false);
            //TODO:写cache
            delegateStock.setEntrustNum(iaStock.getAvailableNum());
            //delegateStock.setEntrustPrice(curPrice);
            delegateStock.setDelegateMethod(1);// 市价委托
            return delegateStock;
        }
        return null;
    }
    
    private float getStockProfit(IAStock stock) {
        
        double curPrice = (float) stock.getCurPrice();
        double avgCost = stock.getAvgPrice();
        float curProfit = (float) ((curPrice - avgCost) / avgCost); 
        return curProfit;
    }
    
    public List<DelegateStock> buyStockRuleInRunday(Map<String, StockSignal> sigMap, UserInfo ia, double freeMoney,
            Collection<IAStock> stocks) {
        return Collections.emptyList();
    }

    public ArrayList<DelegateStock> sellStockRuleInRunday(Map<String, StockSignal> sigMap, UserInfo ia,
            List<DelegateStock> needSellStocks) {
        return null;
    }

    public static float getY1() {
        return Y1;
    }

    public static void setY1(float y1) {
        Y1 = y1;
    }
    
    public static float getY2() {
        return Y2;
    }

    public static void setY2(float y2) {
        Y2 = y2;
    }
    
    public static float getZ() {
        return Z;
    }

    public static void setZ(float z) {
        Z = z;
    }

    public static void configure(Config cfg) {
        Y1 = cfg.getFloat("Rule.Y1",0.05f);
        Y2 = cfg.getFloat("Rule.Y2",0.03f);
        Z = cfg.getFloat("Rule.Z",-0.02f);
    }
}