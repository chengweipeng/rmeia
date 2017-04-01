package com.ism.rmeia.day.strategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ism.market.KLine;
import com.ism.rmeia.bean.DelegateStock;
import com.ism.rmeia.bean.IAStock;
import com.ism.rmeia.bean.StockSignal;
import com.ism.rmeia.bean.UserInfo;
import com.ism.rmeia.enumeration.TradingDayType;

/**
 * 调仓日相关规则
 *
 * @author wx
 * 2016/08/25
 * @versin 1.1.6 增加U-ISM处理
 * @since 1.1.6
 */
public class UIsmRunDayStrategy extends AbstractStrategy {
    private final static Logger logger = LoggerFactory.getLogger(UIsmRunDayStrategy.class);
    public static TradingDayType dayType = TradingDayType.R_DAY;
    public UIsmRunDayStrategy(UserInfo userInfo, Collection<IAStock> stocks, Map<String, StockSignal> sigMap,final Map<String, List<KLine>> kLinesMap){
        super(userInfo, stocks, sigMap,kLinesMap);
    }

    /**
     * 暂时返回为空表
     * @since 1.1.6
     */
    @Override
    public List<DelegateStock> produceDelegateStocks() {
        return Collections.emptyList();
    }

    public ArrayList<DelegateStock> buyStockRuleInRunday(Map<String, StockSignal> sigMap, UserInfo ia,
                                                          double freeMoney, Collection<IAStock> stocks) {
       throw new UnsupportedOperationException("U-ism cannot buy");
    }

    public ArrayList<DelegateStock> sellStockRuleInRunday(Map<String, StockSignal> sigMap, UserInfo ia,
                                                           List<DelegateStock> needSellStocks) {
        throw new UnsupportedOperationException("U-ism cannot sell");
    }
}