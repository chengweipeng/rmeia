package com.ism.market.timer;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ism.market.hbase.KLineGetUtil;

public class HbaseGetKLinerTimer {
    private final static Logger logger = LoggerFactory.getLogger(HbaseGetKLinerTimer.class);
    static HbaseGetKLinerTimer me = null;
    
    Timer t = new Timer();
    KLineGetUtil klineGet = new KLineGetUtil();
    GetTask hbaseGet = new GetTask();
    int periodInMinutes = 1;
    int X=35;
    

    private HbaseGetKLinerTimer() {

    }
    public static HbaseGetKLinerTimer getInstance() {
        if (me == null) {
            synchronized (HbaseGetKLinerTimer.class) {
                if (me == null) {
                    me = new HbaseGetKLinerTimer();
                }
            }
        }
        return me;
    }
    public void setPeriod( int period,TimeUnit unit){
        this.periodInMinutes = (int)unit.toMinutes(period);
    }
    public int getX() {
        return X;
    }
    /**
     * 设置参数X
     * @param x
     */
    public void setX(int x) {
        X = x;
    }

    public void start() {
        try{
            t.scheduleAtFixedRate(hbaseGet, 5*1000, periodInMinutes*60*1000L);
        }catch(Exception ex){
            logger.error("[HbaseGetKLinerTimer:schedule] ex={}",ex);
        }
    }

    public static class GetTask extends TimerTask {
        public GetTask() {
        }

        @Override
        public void run() {
            try{
                KLineGetUtil.extractKLinesFromHBase(me.X);
            }catch(Throwable t){
                logger.error("[HbaseGetTask:run] throwable={}",t);
            }
        }


    }

}
