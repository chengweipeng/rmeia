package com.ism.rmeia;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.ism.rmeia.day.strategy.*;
import com.ism.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.ism.market.KLine;
import com.ism.market.hbase.KLineGetUtil;
import com.ism.rmeia.bean.DelegateStock;
import com.ism.rmeia.bean.IABasic;
import com.ism.rmeia.bean.IAStock;
import com.ism.rmeia.bean.StockInstruction;
import com.ism.rmeia.bean.StockSignal;
import com.ism.rmeia.bean.UserInfo;
import com.ism.rmeia.bean.UserStockInstruction;
import com.ism.rmeia.cache.loader.SimpleCacheLoader;
import com.ism.rmeia.enumeration.IsmType;
import com.ism.rmeia.output.InstructionProduce;
import com.ism.rmeia.task.GetSignalTimer;

public class SignalTask implements Callable<Boolean>, Runnable {
    private final static Logger logger = LoggerFactory.getLogger(SignalTask.class);
    Map<String, StockSignal> sigMap;
    final UserInfo userInfo;
    Map<String, List<KLine>> kLinesMap;

    private long operationTime;// 操作时间
    InstructionProduce<UserStockInstruction> producer;

    String openDayClsName;
    String runDayClsName;
    String closeDayClsName;


    RmeiaStrategyFactory rmeiaStrategyFactory;
    int X;

    public SignalTask(final UserInfo userInfo) {
        this(null,userInfo);
    }
    public SignalTask(final Map<String, StockSignal> sigMap, final UserInfo userInfo) {
        this(sigMap,userInfo,null);
    }
    public SignalTask(final Map<String, StockSignal> sigMap, final UserInfo userInfo,final Map<String, List<KLine>> kLinesMap) {
        this.sigMap = sigMap;
        this.userInfo = userInfo;
        this.kLinesMap  = kLinesMap; 
        setOperationTime(System.currentTimeMillis());
    }

    public void setProducer(InstructionProduce<UserStockInstruction> producer) {
        this.producer = producer;
    }

    // Map<String, Float> stockPriceMap = new HashMap<String, Float>();
    public void run() {
        logger.debug("SignalTask called");
        Preconditions.checkNotNull(userInfo, "userInfo can not be null");
        Preconditions.checkNotNull(producer, "producer can not be null");
        try {
            // 获取用户信息
            if (sigMap == null) {
                sigMap = GetSignalTimer.getInstance().getSIGE();
            }
            if (kLinesMap == null || kLinesMap.isEmpty()) {
                kLinesMap = SimpleCacheLoader.getCache().getKLines();
            }

            if(rmeiaStrategyFactory == null){
                rmeiaStrategyFactory = RmeiaStrategyFactory.getInstance();
            }
            // 获取市场价格
            Collection<DelegateStock> delegateStockList = produceStockOrder(sigMap, userInfo, kLinesMap);

            if (delegateStockList != null && !delegateStockList.isEmpty()) {
                UserStockInstruction userStockInstruction = new UserStockInstruction();
                for (DelegateStock delegateStock : delegateStockList) {
                    // producer.produce(delegateStock);
                    StockInstruction stockInstruction = new StockInstruction();
                    stockInstruction.setStockCode(new String(delegateStock.getStockCode()));
                    stockInstruction.setBuy((byte) (delegateStock.isBuyDir() ? 1 : 0));
                    stockInstruction.setEntrust_num(delegateStock.getEntrustNum());
                    // 价格单位是分，这样就是整数
                    stockInstruction.setPrice((int) (delegateStock.getEntrustPrice() * 100));
                    stockInstruction.setMethod((byte) delegateStock.getDelegateMethod());
                    stockInstruction.setLiveTime((short) 15);
                    userStockInstruction.getOrders().add(stockInstruction);
                    // stockInstruction.setBuy(delegateStock.get);
                    // stockInstruction.setLiveTime(liveTime);
                }
                userStockInstruction.setSource(userInfo.getSource());
                userStockInstruction.setIsm(userInfo.getBasic().getIsm_id());
                userStockInstruction.setUid(userInfo.getBasic().getUser_id());
                userStockInstruction.setMsgId(userInfo.getBasic().getMsgId());
                producer.produce(userStockInstruction);
            } else {
                doResponseWhenNothingToDo();
                return;
            }
        } catch (Throwable t) {
            doResponseWhenNothingToDo();
            logger.error("[SignalTask:run]{}",t.getMessage());
        } finally {

        }
    }
    /**
     * 
     */
    private void doResponseWhenNothingToDo() {
        logger.debug("nothing to do:ismid={},user={}",userInfo.getBasic().getIsm_id(),userInfo.getBasic().getUser_id());
        UserStockInstruction noopInstruction = new UserStockInstruction();
        noopInstruction.setIsm(userInfo.getBasic().getIsm_id());
        noopInstruction.setUid(userInfo.getBasic().getUser_id());
        noopInstruction.setMsgId(userInfo.getBasic().getMsgId());
        noopInstruction.setSource(userInfo.getSource());
        producer.produce(noopInstruction);
    }

    public Collection<DelegateStock> produceStockOrder(Map<String, StockSignal> sigMap, UserInfo userInfo, Map<String, List<KLine>> kLinesMap) {
        logger.debug("calc delegate orders. user={}", userInfo.getBasic().getUser_id());
        List<IAStock> stocks = userInfo.getStocks();
        if (stocks == null) {
            logger.info("[SignalTask:run]stocks is null!");
            return new ArrayList<DelegateStock>();
        }
        IABasic basic = userInfo.getBasic();
        AbstractStrategy strategy = null;
        //先判断是否是平仓日，这样即使其他条件都满足（如建仓）也不会触发其他

        if (basic.isCloseDay()) {
            // 平仓日
            //String closeClsName = cfg.getString(RMEIA_STRATEGY_DEFAULT_OPENDAY, "com.ism.rmeia.day.strategy.OpenDayStrategy");
            strategy =  rmeiaStrategyFactory.newInstance(closeDayClsName,userInfo, stocks, sigMap,kLinesMap);
            //strategy = new CloseDayStrategy(userInfo, stocks, sigMap,kLinesMap);
        } 
        else if (basic.isOpenDay()) {
            // 开仓日
            strategy =  rmeiaStrategyFactory.newInstance(openDayClsName,userInfo, stocks, sigMap,kLinesMap);
            //strategy = new OpenDayStrategy(userInfo, stocks, sigMap,kLinesMap);
        } else {
            // 调仓日,找到可能需要卖出的股票，并计算需要卖出的手数
            if(IsmType.isUIsm(userInfo.getBasic().getIsm_id())){
                strategy = new UIsmRunDayStrategy(userInfo, stocks, sigMap,kLinesMap);
            }else{
                strategy =  rmeiaStrategyFactory.newInstance(openDayClsName,userInfo, stocks, sigMap,kLinesMap);
                //strategy = new RunDayStrategy(userInfo, stocks, sigMap,kLinesMap);
            }
        }
        strategy.setOperationTime(getOperationTime());
        return strategy.produceDelegateStocks();
    }

    @Override
    public Boolean call() throws Exception {
        try {
            run();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("rmeia error {}",e);
            return false;
        } finally {

        }
    }
    /**
     * 
     * @return 操作时间
     */
    public long getOperationTime() {
        return operationTime;
    }

    /**
     * 设置平仓操作时间
     * 
     * @param operationTime 如果operationTime>0 那么设置时间
     */
    // @VisibleForTesting
    public void setOperationTime(long operationTime) {
        if(operationTime>0){
            this.operationTime = operationTime;
        }
    }

    public String getOpenDayClsName() {
        return openDayClsName;
    }

    public void setOpenDayClsName(String openDayClsName) {
        this.openDayClsName = openDayClsName;
    }

    public String getRunDayClsName() {
        return runDayClsName;
    }

    public void setRunDayClsName(String runDayClsName) {
        this.runDayClsName = runDayClsName;
    }

    public String getCloseDayClsName() {
        return closeDayClsName;
    }

    public void setCloseDayClsName(String closeDayClsName) {
        this.closeDayClsName = closeDayClsName;
    }
    public RmeiaStrategyFactory getRmeiaStrategyFactory() {
        return rmeiaStrategyFactory;
    }

    public void setRmeiaStrategyFactory(RmeiaStrategyFactory rmeiaStrategyFactory) {
        this.rmeiaStrategyFactory = rmeiaStrategyFactory;
    }

}