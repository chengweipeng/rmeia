package com.ism.rmeia.day.strategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ism.enumeration.DELEGATE_METHOD;
import com.ism.market.KLine;
import com.ism.rmeia.bean.DelegateStock;
import com.ism.rmeia.bean.IAStock;
import com.ism.rmeia.bean.StockSignal;
import com.ism.rmeia.bean.UserInfo;
import com.ism.rmeia.enumeration.TradingDayType;
import com.ism.rmeia.rule.SellRule5;

/**
 * 调仓日相关规则
 *
 * @author wx
 */
public class CloseDayStrategy extends AbstractStrategy {
    private final static Logger logger = LoggerFactory.getLogger(CloseDayStrategy.class);
    public static TradingDayType dayType = TradingDayType.C_DAY;
    public CloseDayStrategy(final UserInfo userInfo, final Collection<IAStock> stocks,
            final Map<String, StockSignal> sigMap,final Map<String, List<KLine>> kLines) {
        super(userInfo, stocks, sigMap,kLines);
    }

    @Override
    public List<DelegateStock> produceDelegateStocks() {
        logger.debug("{}-{}",dayType, userInfo);
        return sellStockRule();
    }

    public List<DelegateStock> buyStockRule() {
        return new ArrayList<>();
    }

    public List<DelegateStock> sellStockRule() {
        List<DelegateStock> delegateList = new ArrayList<>();

        // 设置操作时间
        if (after(getOperationTime(),14,45)) {
            logger.info("Close:SellRule.after.ismid={}",userInfo.getBasic().getIsm_id());
            return sellAfternoonInCloseDay(stocks);
        } else {
            // 市价平仓
            for (IAStock iaStock : stocks) {
                if (iaStock.getAvailableNum() > 0) {
                    DelegateStock delegateStock = sellRul5(iaStock);
                    if(delegateStock != null){
                        delegateList.add(delegateStock);
                    }
                }
            }
            logger.info("Close:SellRule4.ismid={},{}",userInfo.getBasic().getIsm_id(),delegateList.size()); 
        }
        return delegateList;
    }

    /**
     * 到下午，全部市价平仓
     *
     * @param stocks
     * @return 需要委托卖出的股票集合(包括股价、委托数量、委托方式)
     */
    ArrayList<DelegateStock> sellAfternoonInCloseDay(Collection<IAStock> stocks) {
        ArrayList<DelegateStock> needSellStocks = new ArrayList<>();
        for (IAStock stock : stocks) {
            if (stock.getAvailableNum() > 0) {
                DelegateStock sellStock = new DelegateStock(stock);
                sellStock.setEntrustNum(stock.getAvailableNum());
                sellStock.setEntrustPrice(stock.getCurPrice());
                sellStock.setBuyDir(false);
                sellStock.setDelegateMethod(DELEGATE_METHOD.SHIJIA_U.getValue());
                needSellStocks.add(sellStock);
            }
        }
        return needSellStocks;
    }


    public DelegateStock sellRul5(IAStock iaStock){
        double curPrice = iaStock.getCurPrice();
        List<KLine> kLines = kLinesMap.get(iaStock.getStockCode());
        if (kLines != null && kLines.size() < 5) {
            logger.warn("[Close:SellRule] k lines is illegal,stockcode={},kLINE.SIZE={}",iaStock.getStockCode(),kLines.size());
            return null;
        }
        String stockCode = iaStock.getStockCode();
        CloseDayMarketParameter param = genCloseDayParameter(this.kLinesMap,stockCode);
        boolean sell = SellRule5.sell(param);
        logger.debug("Close:SellRule.is={},stockcode={}",sell,stockCode);
        if(sell){
            DelegateStock delegateStock  =  new DelegateStock(iaStock);
            delegateStock.setBuyDir(false);
            //TODO:写cache
            delegateStock.setEntrustNum(iaStock.getAvailableNum());
            delegateStock.setEntrustPrice(curPrice);
            delegateStock.setDelegateMethod(1);// 市价委托
            return delegateStock;
        }
        return null;
    }
}
