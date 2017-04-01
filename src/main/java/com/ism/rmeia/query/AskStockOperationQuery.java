package com.ism.rmeia.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;

import com.ism.market.KLine;
import com.ism.market.hbase.KLineGetUtil;
import com.ism.rmeia.bean.DelegateStock;
import com.ism.rmeia.bean.IABasic;
import com.ism.rmeia.bean.IAStock;
import com.ism.rmeia.bean.StockInstruction;
import com.ism.rmeia.bean.StockSignal;
import com.ism.rmeia.bean.UserInfo;
import com.ism.rmeia.bean.UserStockInstruction;
import com.ism.rmeia.day.strategy.AbstractStrategy;
import com.ism.rmeia.day.strategy.RunDayStrategy;
import com.ism.rmeia.rule.SoldRule1;

public class AskStockOperationQuery {

    Map<String, UserStockInstruction> userStockInstructionMap = new HashMap<String, UserStockInstruction>();
    UserInfo userInfo = new UserInfo();
    static AtomicInteger seq = new AtomicInteger();
    public static class AskStock{
        float cost;//成本价
    }

    private long operationTime;// 操作时间
    public AskStockOperationQuery(){
        setOperationTime(System.currentTimeMillis());
    }
    public AskStockOperationQuery(final UserInfo userInfo,final Map<String, UserStockInstruction> userStockInstructionMap){
        setOperationTime(System.currentTimeMillis());
        this.userInfo = userInfo;
        this.userStockInstructionMap = userStockInstructionMap;
    }
    /**
     * @param stockSignalMap SIG&E信息
     * @param priceMap 行情信息
     * @param stocks 股票持仓
     * @return 操作指令 MAP
     */
    public Map<String, UserStockInstruction> getStockOperation(Map<String, StockSignal> stockSignalMap,Map<String, Integer> priceMap,Collection<String> stocks) {
        // 调仓日,找到可能需要卖出的股票，并计算需要卖出的手数
        //<1>设置用户信息
        String userId = "9999";
        
        IABasic basic = new IABasic();
        basic.setUser_id(userId);//TODO:
        basic.setCloseDay(false);
        basic.setDiffOpenDay(1);
        basic.setIa_id(0);
        basic.setIa_status((byte)1);
        basic.setTotalFreeMoney(20000);
        basic.setTotalMoney(20000);
        basic.setIsm_id("ZX");
        basic.setMsgId(String.valueOf(seq.getAndIncrement()));
        userInfo.setBasic(basic);
        ArrayList<DelegateStock> sellDelegates = genStockSellInstruction(userInfo,stockSignalMap,priceMap, stocks);
        //计算该买入的
        List<DelegateStock> buyDelegates = genStockBuyInstruction(userInfo,stockSignalMap,priceMap, stocks);
                
        //汇总买入卖出指令
        UserStockInstruction sellInstuction = toOperationInstructions(userInfo,sellDelegates);
        UserStockInstruction buyInstuction = toOperationInstructions(userInfo,buyDelegates);
        mergeInstructions(userInfo, userStockInstructionMap, sellInstuction);
        mergeInstructions(userInfo, userStockInstructionMap, buyInstuction);
        return userStockInstructionMap;
    }


    public UserStockInstruction toOperationInstructions(UserInfo userInfo,List<DelegateStock> sellStockInstructions) {
        //Map<String, UserStockInstruction> userStockInstructionMap = new HashMap<String, UserStockInstruction>();
        UserStockInstruction newUserInstockInstuctions = new UserStockInstruction();
        newUserInstockInstuctions.setUid(userInfo.getBasic().getUser_id());
        newUserInstockInstuctions.setIsm(userInfo.getBasic().getIsm_id());
        newUserInstockInstuctions.setMsgId(userInfo.getBasic().getMsgId());
        ArrayList<StockInstruction> orders = new ArrayList<>();
        for(DelegateStock delegateStock:sellStockInstructions){
            String stockCode = delegateStock.getStockCode();
            //delegateStock
            StockInstruction e = new StockInstruction();
            e.setBuy((byte)(delegateStock.isBuyDir()?1:0));
            e.setStockCode(stockCode);
            e.setPrice((int)(delegateStock.getEntrustPrice()*100));
            e.setMethod((byte) (delegateStock.getDelegateMethod()&0xF));
            e.setLiveTime((short)(15));
            orders.add(e);
        }
        newUserInstockInstuctions.getOrders().addAll(orders);
        return newUserInstockInstuctions;
    }

    /**
     * 根据用户信息合并操作指令，并存储在{@code userStockInstructionMap}中
     * @param userInfo [IN]
     * @param userStockInstructionMap [IN&OUT] 
     * @param newUserInstockInstuctions [IN]
     */
    public void mergeInstructions(final UserInfo userInfo,Map<String, UserStockInstruction> userStockInstructionMap,final UserStockInstruction newUserInstockInstuctions){
        String userId = userInfo.getBasic().getUser_id();
        if(StringUtils.isEmpty(userId)){
            userId = "unkown";
        }
        UserStockInstruction  oldInstruction = userStockInstructionMap.get(userId);
        if(oldInstruction == null){
            userStockInstructionMap.put(userInfo.getBasic().getUser_id(), newUserInstockInstuctions);
        }
        else{
            oldInstruction.getOrders().addAll(newUserInstockInstuctions.getOrders());
        }
    }
    
    private ArrayList<DelegateStock> genStockSellInstruction(UserInfo userInfo,Map<String, StockSignal> stockSignalMap,Map<String, Integer> priceMap, Collection<String> stocks) {
        Collection<IAStock> iaStockList = new ArrayList<>();
        for(String stockCode:stocks){
            IAStock stock = new IAStock();
            stock.setAvailableNum(500);
            stock.setNeedOpenBuyNum(1000);
            Integer price = priceMap.get(stockCode);
            if(price == null){
                continue;
            }
            stock.setCurPrice(price*0.01f);
            stock.setAvgPrice(price*0.01f);
            stock.setOwnNum(500);
            stock.setStockCode(stockCode);
            iaStockList.add(stock);
        }
        RunDayStrategy strategy = null;
        Map<String, List<KLine>> kLinesMap = null;
        if (kLinesMap == null) {
            try {
                kLinesMap = KLineGetUtil.extractKLinesFromHBase(35);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        strategy = new RunDayStrategy(userInfo, iaStockList, stockSignalMap,kLinesMap);
        
        strategy.setOperationTime(getOperationTime());
        //ArrayList<DelegateStock>
        //计算获取该卖出的
        ArrayList<DelegateStock> sellStock = SoldRule1.getNeedSellStock(userInfo, iaStockList, stockSignalMap);
        strategy.sellStockRuleInRunday(stockSignalMap, userInfo, sellStock);
        return sellStock;
    }

    /**
     * 会将成本价设置为当前价
     * @param userInfo
     * @param stockSignalMap
     * @param priceMap
     * @param stocks
     * @return 返回买入的股数（如果不是买入，说明有bug)
     */
    private List<DelegateStock> genStockBuyInstruction(UserInfo userInfo,Map<String, StockSignal> stockSignalMap,Map<String, Integer> priceMap, Collection<String> stocks) {
        Collection<IAStock> iaStockList = new ArrayList<IAStock>();
        for(String stockCode:stocks){
            IAStock stock = new IAStock();
            stock.setAvailableNum(0);
            stock.setNeedOpenBuyNum(1000);
            Integer hqPrice = priceMap.get(stockCode);
            stock.setCurPrice(hqPrice);
            stock.setAvgPrice(hqPrice);
            stock.setOwnNum(0);
            stock.setStockCode(stockCode);
            iaStockList.add(stock);
        }
        RunDayStrategy strategy = null;
        strategy = new RunDayStrategy(userInfo, iaStockList, stockSignalMap,null);
        strategy.setOperationTime(getOperationTime());
        //计算获取该卖出的
        List<DelegateStock> buyStock = strategy.buyStockRuleInRunday(stockSignalMap, userInfo, userInfo.getBasic().getTotalFreeMoney(), iaStockList);
        return buyStock;
    }
    
    public Map<String, UserStockInstruction> getStockOperation(Map<String, StockSignal> stockSignalMap,Map<String, Integer>priceMap, String ...stocks) {
        return getStockOperation(stockSignalMap,priceMap,Arrays.asList(stocks));
    }
    
    public Map<String, UserStockInstruction> getStockOperation(Map<String, StockSignal> stockSignalMap,Map<String, Integer>priceMap, AskStock ...stocks) {
        throw new UnsupportedOperationException("not implement yet.");
    }
    
    public long getOperationTime() {
        return operationTime;
    }
    public void setOperationTime(long operationTime) {
        this.operationTime = operationTime;
    }
}
