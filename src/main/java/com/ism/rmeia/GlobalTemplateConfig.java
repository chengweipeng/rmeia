package com.ism.rmeia;

import java.util.Map;
import java.util.Timer;

import com.ism.db.DBHelper;
import com.ism.market.timer.HbaseGetKLinerTimer;
import com.ism.rmeia.cache.loader.SimpleCacheLoader;
import com.ism.rmeia.day.strategy.RunDayStrategy;
import com.ism.rmeia.output.AsyncHttpSendStringUtils;
import com.ism.rmeia.output.HttpStockInstructionProducer;
import com.ism.rmeia.rule.BuyRule2;
import com.ism.rmeia.task.ConnectonMonitor;
//import com.ism.rmeia.task.GetPredictTimer;
//import com.ism.rmeia.task.GetSignalTimer;
import com.ism.rmeia.task.GetStockPriceTimer;
import com.ism.rmeia.util.Enviroment;
import com.ism.util.Config;

public class GlobalTemplateConfig {

    boolean _devMode;

    Timer t = new Timer("gTm");
    Config config;

    public GlobalTemplateConfig(Config cfg) {
        this.config = cfg;
    }

    public GlobalTemplateConfig() {
        config = Config.getInstance();
    }

    public Config getConfig() {
        return config;
    }

    /**
     * 全局设置
     */
    public void init() {
        devMode(config.getBoolean("dev.mode", false));
        DBHelper.getInstance().configure(config);
        Enviroment.config(config);
        ConnectonMonitor.getInstance().configure(config);
        HttpStockInstructionProducer.getInstance().setUrl(Enviroment.ia_send_url);
        BuyRule2.setSharpRatioThreshold(config.getFloat("rule.sharpRatioThreshold", 1.0f));
        GetStockPriceTimer.getInstance().setAll_stock_url(config.getString("price.getall.url"));//http://192.168.10.139:5437/queryallstock?format=json&version=2.0&compress_type=gzip")
        SimpleCacheLoader.getInstance().configure(config);
        HbaseGetKLinerTimer.getInstance().setX(config.getInt("Rule.X",35));
        RunDayStrategy.setY1(config.getFloat("Rule.Y1",0.05f));
        RunDayStrategy.setY2(config.getFloat("Rule.Y2",0.02f));
        RunDayStrategy.setZ(config.getFloat("Rule.Z",-0.02f));
        AsyncHttpSendStringUtils.getInstance().configure(config);
        
    }

    /**
     * 全局设置
     */
    public void init(Map<String, String> map) {
        this.config.putAll(map);
        init();
    }

    /**
     * 服务启动初始化
     */
    public void start() {
        //GetSignalTimer task = GetSignalTimer.getInstance();
        //t.schedule(task, 0, Enviroment.sigReadPeriod * 1000L);
        GetStockPriceTimer getStockPriceTask = GetStockPriceTimer.getInstance();
        getStockPriceTask.run();
        //t.scheduleAtFixedRate(getStockPriceTask, 0, (long)(2.7*60*1000));
        //GetPredictTimer  preictTask = GetPredictTimer.getInstance();
        //t.schedule(preictTask, 0,Enviroment.sigReadPeriod * 1000L);
      
        //20161018 去掉数据库监控
        // ConnectonMonitor.getInstance().start();
        
        SimpleCacheLoader.getInstance().start();
        //HbaseGetKLinerTimer.getInstance().start();
    }
    public boolean devMode() {
        return _devMode;
    }
    /**
     * 设置开发模式
     *
     * @param bDevMode
     */
    public void devMode(boolean bDevMode) {
        _devMode = bDevMode;
    }
}
