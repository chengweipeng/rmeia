package com.ism.rmeia.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import com.ism.market.KLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.ism.rmeia.SignalTask;
import com.ism.rmeia.bean.IABasic;
import com.ism.rmeia.bean.IAInputStock;
import com.ism.rmeia.bean.IAStock;
import com.ism.rmeia.bean.InputIAInfo;
import com.ism.rmeia.bean.StockSignal;
import com.ism.rmeia.bean.UserInfo;
import com.ism.rmeia.bean.UserStockInstruction;
import com.ism.rmeia.execute.TaskExecutors;
import com.ism.rmeia.output.AbstractProducer;
import com.ism.rmeia.output.HttpStockInstructionProducer;
import com.ism.rmeia.util.Enviroment;

/**
 * 每个消息处理 输入IA信息
 *
 * @author wx
 */
public class MessageConsume implements Callable<Boolean> {
    private final static Logger logger = LoggerFactory.getLogger(MessageConsume.class);
    final InputIAInfo iaInfo;
    Map<String, StockSignal> sigMap;
    AbstractProducer<UserStockInstruction> producer;
    Map<String,List<KLine>> kLineMap;
    Future<?> future;
    private long operationTime;// 操作时间
    String openDayClsName="com.ism.rmeia.day.strategy.OpenDayStrategy";
    String runDayClsName="com.ism.rmeia.day.strategy.RunDayStrategy";
    String closeDayClsName="com.ism.rmeia.day.strategy.CloseDayStrategy";

    public MessageConsume(String message, final Map<String, StockSignal> sigMap, final Map<String, List<KLine>> kLineMap) {
        this(JSON.parseObject(message,InputIAInfo.class),sigMap,kLineMap);
    }
    public MessageConsume(InputIAInfo info) {
        this(info,null);
    }
    public MessageConsume(InputIAInfo info, final Map<String, StockSignal> sigMap) {
      this(info,sigMap,null);
    }
    public MessageConsume(InputIAInfo info, final Map<String, StockSignal> sigMap,final Map<String, List<KLine>> kLineMap) {
        this.iaInfo = info;
        this.sigMap = sigMap;
        this.kLineMap = kLineMap;
        setOperationTime(System.currentTimeMillis());
    }

    @Override
    public Boolean call() throws Exception {
        try {
            logger.debug("MessageConsume call");
            UserInfo userInfo = new UserInfo();
            IABasic basic = new IABasic();
            basic.setIsm_id(new String(iaInfo.getIsm()));
            basic.setUser_id(new String(iaInfo.getUid()));
            basic.setTotalMoney(iaInfo.getTotoalMoney());
            basic.setTotalFreeMoney(iaInfo.getTotalFreeMoney());
            basic.setTotalMacketValue(iaInfo.getTotalMacketValue());
            basic.setDiffOpenDay(iaInfo.getAfterBuildDay());
            basic.setCloseDay((iaInfo.getIsCloseDay() != 0));
            basic.setMsgId(new String(iaInfo.getMsgId()));
            basic.setSource(iaInfo.getSource());
            
            for (IAInputStock stock : iaInfo.getStock()) {
                IAStock iaStock = new IAStock();
                iaStock.setAvailableNum(stock.getAvailable_num());
                iaStock.setAvgPrice(stock.getAvg_cost());
                iaStock.setNeedOpenBuyNum(stock.getInitBuildNum());
                iaStock.setOwnNum(stock.getStock_num());
                // 价格需要从分转为元
                iaStock.setCurPrice(stock.getLast_price() * 0.01);
                iaStock.setStockCode(new String(stock.getStockCode()));
                userInfo.getStocks().add(iaStock);
            }
            userInfo.setBasic(basic);
            logger.debug("user info={}", userInfo);
            SignalTask task = new SignalTask(sigMap, userInfo,kLineMap);
            task.setOpenDayClsName(openDayClsName);
            task.setRunDayClsName(runDayClsName);
            task.setCloseDayClsName(closeDayClsName);
            // if test mode
            task.setOperationTime(getOperationTime());
            if (producer == null) {
                 task.setProducer(HttpStockInstructionProducer.getInstance());
            } else {
                task.setProducer(producer);
            }
            if(Enviroment.devMode){
                task.call();
            }
            else{
                future = TaskExecutors.getInstance().addTask(task);
            }
            return true;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return false;
    }

    /**
     * 设置rmeia向哪个发送消息
     * 
     * @return procuder
     */
    public AbstractProducer<UserStockInstruction> getProducer() {
        return producer;
    }

    public void setProducer(AbstractProducer<UserStockInstruction> producer) {
        this.producer = producer;
    }

    /**
     * 返回异步处理的future
     */
    public Future<?> getFuture() {
        return future;
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
     * @param operationTime
     */
    // @VisibleForTesting
    public void setOperationTime(long operationTime) {
        this.operationTime = operationTime;
    }
    
    /**
     * @return SIG集 key=stockcode，value=sig
     */
    public Map<String, StockSignal> getSigMap(){
    	return sigMap;
    }
    public void setSigMap(Map<String, StockSignal> sigMap){
    	this.sigMap = sigMap;
    }
    public void setOpenDayClsName(String openDayClsName) {
        this.openDayClsName = openDayClsName;
    }

    public String getOpenDayClsName() {
        return openDayClsName;
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
}