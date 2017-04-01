package com.ism.rmeia.day.strategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.ism.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ism.enumeration.DELEGATE_METHOD;
import com.ism.market.KLine;
import com.ism.market.hbase.KLineGetUtil;
import com.ism.rmeia.bean.DelegateStock;
import com.ism.rmeia.bean.IAStock;
import com.ism.rmeia.bean.StockSignal;
import com.ism.rmeia.bean.UserInfo;
import com.ism.rmeia.enumeration.TradingDayType;
import com.ism.rmeia.rule.DefaultOpenDayStockRule;
import com.ism.rmeia.rule.StockRule;

/**
 * 建仓日处理逻辑
 * 
 * @author wx
 * @version 1.0
 * @since 1.0
 */
public class OpenDayStrategy extends AbstractStrategy {
    private final static Logger logger = LoggerFactory.getLogger(OpenDayStrategy.class);
    public static TradingDayType dayType = TradingDayType.O_DAY;
    private static int DEFAULT_MAX_OPEN_BUY_NUM = 2000;

    //StockRule rule;
    StockRule<KLine> rule = new DefaultOpenDayStockRule();
    OpenDayMarketParameter openDayMarketParameter;

    public OpenDayStrategy(UserInfo userInfo, Collection<IAStock> stocks, Map<String, StockSignal> sigMap,
            final Map<String, List<KLine>> kLinesMap) {
        super(userInfo, stocks, sigMap, kLinesMap);
    }

    @Override
    public List<DelegateStock> produceDelegateStocks() {
        logger.debug("dayType={}-userInfo={}", dayType, userInfo);
        return buyStockRule();
    }

    /**
     * 开仓日，计算返回
     *
     * @return 需要委托的股票列表
     */
    public ArrayList<DelegateStock> buyStockRule() {
        ArrayList<DelegateStock> delegateList = new ArrayList<DelegateStock>();
        double freeMoney = userInfo.getBasic().getTotalFreeMoney();
        //String ismId = userInfo.getBasic().getIsm_id();
        //String userId = userInfo.getBasic().getUser_id();
        //String msgId = userInfo.getBasic().getMsgId();
        List<IAStock> zeroBuildStocks = new ArrayList<>();
        
        if (after(getOperationTime(),14,45)) {
            //logger.info("Open:buyRule.after.ismid={}",userInfo.getBasic().getIsm_id());
            return buyAfternoonInOpenDay(stocks,freeMoney);
        }
        else {
            for (IAStock iaStock : stocks) {
                //String stockCode = iaStock.getStockCode();
                if (iaStock.getNeedOpenBuyNum() <= 0) {
                    logger.info("invalid initBuyNum. is 0,set to infinite.");
                    zeroBuildStocks.add(iaStock);
                    continue;
                    // iaStock.setNeedOpenBuyNum(Integer.MAX_VALUE);
                }
                if (iaStock.getOwnNum() < iaStock.getNeedOpenBuyNum()) {
                    DelegateStock delegateStock = buyRule(iaStock, freeMoney);
                    if (delegateStock != null) {
                        delegateList.add(delegateStock);
                    }
                }
            }
            Collections.shuffle(zeroBuildStocks);
            List<DelegateStock> zeroNumChooseList = chooseRandomAfterZeroNum(freeMoney, zeroBuildStocks);
            delegateList.addAll(zeroNumChooseList);
            //logger.info("Open:buyRule4.ismid={},{}",userInfo.getBasic().getIsm_id(),delegateList.size());
        }
        
        
        return delegateList;
    }

    /**
     * 14:45之后全部市价建仓
     *
     */
    ArrayList<DelegateStock> buyAfternoonInOpenDay(Collection<IAStock> stocks,double freeMoney) {
        ArrayList<DelegateStock> needBuyStocks = new ArrayList<>();
        List<IAStock> zeroBuildStocks = new ArrayList<>();
        for (IAStock stock : stocks) {
            if (stock.getNeedOpenBuyNum() <= 0) {
                zeroBuildStocks.add(stock);
                continue;
            }
            
            if (stock.getOwnNum() < stock.getNeedOpenBuyNum()) {
                DelegateStock buyStock = new DelegateStock(stock);
               
                int canBuyNum = (int)((freeMoney+0.005 - Math.max(6, freeMoney*0.003))/stock.getCurPrice()/100);
                int entrustNum = Math.min(stock.getNeedOpenBuyNum() - stock.getOwnNum(), canBuyNum * 100);
                freeMoney -= entrustNum*stock.getCurPrice() + Math.max(6, freeMoney*0.003);
                buyStock.setBuyDir(true);
                buyStock.setEntrustNum(entrustNum);
                buyStock.setEntrustPrice(stock.getCurPrice());
                buyStock.setDelegateMethod(1);// 市价委托
                if(entrustNum > 0){
                    needBuyStocks.add(buyStock);
                }
            }
            
        }
        Collections.shuffle(zeroBuildStocks);
        List<DelegateStock> zeroNumChooseList = chooseRandomAfterZeroNum(freeMoney, zeroBuildStocks);
        needBuyStocks.addAll(zeroNumChooseList);
        return needBuyStocks;
    }
    
        
    /**
     * 这个是容错，当输入初始建仓手数为0时，需要处理为:选择买入一半的股票
     * 
     * @param freeMoney
     *            可用现金
     * @param zeroBuildStocks
     *            建仓手数为0的股票
     * @since 1.0
     * @return 需要委托建仓的股票 <code>ArrayList</code>
     */
    private List<DelegateStock> chooseRandomAfterZeroNum(double freeMoney, List<IAStock> zeroBuildStocks) {
        ArrayList<DelegateStock> delegateList = new ArrayList<>();
        int chooseCount = 0;
        //final String ismId = userInfo.getBasic().getIsm_id();
        //final String userId = userInfo.getBasic().getUser_id();
        for (IAStock iaStock : zeroBuildStocks) {
            if (chooseCount * 2 >= zeroBuildStocks.size()) {
                break;
            }
            String stockCode = iaStock.getStockCode();
            StockSignal sig = sigMap.get(stockCode);
            if (sig == null || isTimeout(sig.sig_tm)) {
                logger.warn("SIG_E,no signal {}", stockCode);
                //continue;
            }
            iaStock.setNeedOpenBuyNum(DEFAULT_MAX_OPEN_BUY_NUM);// 设置最大没入
            if (iaStock.getOwnNum() < iaStock.getNeedOpenBuyNum()) {

                DelegateStock delegateStock = new DelegateStock(iaStock);
                // 2:45之后直接市价委托
                if (after(getOperationTime(), 14, 45)) {
                    int canBuyNum = (int) ((freeMoney + 0.005 - Math.max(6, freeMoney * 0.003)) / iaStock.getCurPrice()
                            / 100);
                    int entrustNum = Math.min(iaStock.getNeedOpenBuyNum() - iaStock.getOwnNum(), canBuyNum * 100);
                    freeMoney -= entrustNum * iaStock.getCurPrice() + Math.max(6, freeMoney * 0.003);
                    delegateStock.setBuyDir(true);
                    delegateStock.setEntrustNum(entrustNum);
                    delegateStock.setEntrustPrice(iaStock.getCurPrice());
                    delegateStock.setDelegateMethod(1);// 市价委托
                    if (entrustNum > 0) {
                        delegateList.add(delegateStock);
                        chooseCount++;
                    }
                } else {
                    delegateStock = buyRule(iaStock, freeMoney);
                    if (delegateStock != null && delegateStock.getEntrustNum() > 0) {
                        delegateList.add(delegateStock);
                        chooseCount++;
                    }
                }

            }
        }
        return delegateList;
    }

//    public List<DelegateStock> sellStockRule() {
//       return Collections.emptyList();
//    }

    /**
     * 获取建仓日参数
     * @return
     */
    public OpenDayMarketParameter getOpenDayMarketParameter() {
        return openDayMarketParameter;
    }

    /**
     * 设置建仓日参数，通常用于测试
     * @param openDayMarketParameter
     */
    public void setOpenDayMarketParameter(OpenDayMarketParameter openDayMarketParameter) {
        this.openDayMarketParameter = openDayMarketParameter;
    }




    public DelegateStock buyRule(IAStock iaStock, double freeMoney){
        double curPrice = iaStock.getCurPrice();
//        ArrayList<KLine> kLines = kLinesMap.get(iaStock.getStockCode());
//        if (kLines != null && kLines.size() < 5) {
//            logger.warn("[Open:buyRule] k lines is illegal,stockcode={},kLINE.SIZE={}",iaStock.getStockCode(),kLines.size());
//            return null;
//        }
        
        String stockCode = iaStock.getStockCode();
        List<KLine> klines = KLineGetUtil.getSeq(kLinesMap, stockCode);
        if(klines.size() < 5 ){
            throw new RuntimeException("K lines init error.");
        }

        boolean buy = rule.judge(klines)==1?true:false;
        logger.debug("Open:buyRule.is={},stockcode={}",buy,stockCode);
        if(buy){
            int canBuyNum = (int)((freeMoney+0.005 - Math.max(6, freeMoney*0.003))/iaStock.getCurPrice()/100);
            //canBuyNum = Math.min(iaStock.getAvailableNum(), canBuyNum);
            //需要买入的手数和可买入手数中，取二者最小值
            int entrustNum = Math.min(iaStock.getNeedOpenBuyNum() - iaStock.getOwnNum(), canBuyNum * 100);
            if(entrustNum <= 0){
                return null; 
            }
            freeMoney -= entrustNum*iaStock.getCurPrice() + Math.max(6, freeMoney*0.003);
            DelegateStock delegateStock  =  new DelegateStock(iaStock);
            delegateStock.setBuyDir(true);
            //TODO:写cache
            delegateStock.setEntrustNum(entrustNum);
            delegateStock.setEntrustPrice(curPrice);
            delegateStock.setDelegateMethod(1);// 市价委托
            return delegateStock;
        }
        return null;
    }
}
