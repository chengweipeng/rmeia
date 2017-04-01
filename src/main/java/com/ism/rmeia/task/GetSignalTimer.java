package com.ism.rmeia.task;

import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ism.rmeia.bean.StockSignal;
import com.ism.rmeia.dto.StockSignalDto;
import com.ism.rmeia.util.Enviroment;

/**
 * 定时获取数据
 *
 * TODO:这个机制需要修改，需要改成2个指针，读写切换方式。当前实现方式的弊端是，如果读取的数据不全，将无法全部更新MAP表，导致读取到过期数据
 * @author wx
 */
public class GetSignalTimer extends TimerTask implements Runnable {
    private final static Logger logger = LoggerFactory.getLogger(GetSignalTimer.class);
    public static int SLICE_IN_SECONDS = 1 * 60;
    static GetSignalTimer me =null;
    //涉及到一方读取，一方写
    ConcurrentHashMap<String, StockSignal> stockSignalMap = new ConcurrentHashMap<String, StockSignal>();

    private GetSignalTimer() {
    }

    @Override
    public void run() {
    	logger.debug("signal timer started");
        StockSignalDto dto = new StockSignalDto();
        try {
            Map<String, StockSignal> result = null;
            if(Enviroment.sigDebugSettime && Enviroment.sigDebugGetTime>0){
                result = dto.getSliceAll(Enviroment.sigDebugGetTime);
            }
            else{
                result = dto.getLatestSliceAll();
            }
            logger.info("getLatestAll slice end {},total={}", System.currentTimeMillis(), result.size());
            if(!Enviroment.devMode){
                //非开发模式，需要先清空数据，然后再重新加载
                //stockSignalMap.clear();
                stockSignalMap.putAll(result);
            }
            else{
                stockSignalMap.putAll(result);
            }
            
        } catch (Throwable e) {
            e.printStackTrace();
            logger.error("getLatestAll slice error:{}",e.getMessage());
            //TODO: 需要向监控服务器发送报告
        }
    }

    public Map<String, StockSignal> getSIGE() {
        return stockSignalMap;
    }

    public static GetSignalTimer getInstance() {
        if(me==null){
            synchronized (GetSignalTimer.class) {
                if(me==null){
                    me = new GetSignalTimer();
                }
            }
        }
        return me;
    }
}
