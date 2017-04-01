package com.ism.rmeia.cache.loader;

import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ism.rmeia.cache.SimpleCache;
import com.ism.util.Config;

/**
 *
 */
public class SimpleCacheLoader {
    private final static Logger logger = LoggerFactory.getLogger(SimpleCacheLoader.class);
    
    private static final String PRICE_HS300_URL = "price.hs300.url";
    public static final java.lang.String yyyyMMdd_FORMAT = "yyyyMMdd";

    private static final String MARKET_1KLINE_URL = "market.1kline.url";
   
    static ScheduledExecutorService es = Executors.newSingleThreadScheduledExecutor();

    Config config = Config.getInstance();
    long hs300_period = 60 * 1000L;

    SimpleCache cache = new SimpleCache();

    FecthStockTask fecthStockTask = new FecthStockTask(); // 每分钟获取1次
    Fetch1MinKlineTask fetch1MinKlineTask = new Fetch1MinKlineTask();
    InitCacheTask cacheTask = new InitCacheTask();

    static SimpleCacheLoader me = new SimpleCacheLoader();
    // url =1
    // http://192.168.10.139:5500/query?stockcode=300250&format=json&version=2.0

    public static SimpleCacheLoader getInstance() {
        return me;
    }
   public void start() {
       
       cacheTask.configure(config.imutableMap());
       cacheTask.setCache(cache);
       
       long initialDelay = 0;
       // 9点刷新，主要更新股票基本信息
       Calendar c = Calendar.getInstance();
       c.set(Calendar.HOUR_OF_DAY, 9);
       c.set(Calendar.MINUTE, 05);
       long curTime = System.currentTimeMillis();
       initialDelay = c.getTimeInMillis() - curTime;
       // 如果initialDelay小于0，会立即执行
       es.scheduleAtFixedRate(cacheTask, initialDelay, 86400*1000L, TimeUnit.MILLISECONDS);
       
       c.set(Calendar.HOUR_OF_DAY, 17);
       c.set(Calendar.MINUTE, 05);
       initialDelay = c.getTimeInMillis() - curTime;
       es.scheduleAtFixedRate(cacheTask, initialDelay, 86400*1000L, TimeUnit.MILLISECONDS);
       
        // 每分钟计算1次
        fecthStockTask.setUrl(config.getString(PRICE_HS300_URL));
        fecthStockTask.setCache(cache);
        fetch1MinKlineTask.setCache(cache);
        fetch1MinKlineTask.setUrl(config.getString(MARKET_1KLINE_URL));
        logger.info("HS300={}",config.getString(PRICE_HS300_URL));
        logger.info("market={}",config.getString(MARKET_1KLINE_URL));
        
        es.scheduleAtFixedRate(fecthStockTask, 1000L, hs300_period, TimeUnit.MILLISECONDS);
        es.scheduleAtFixedRate(fetch1MinKlineTask, 1000L, hs300_period, TimeUnit.MILLISECONDS);
    }
    public static SimpleCache getCache() {
        return me.cache;
    }
    public void configure(Config config) {
        this.config = config;
    }
    public long getHs300_period() {
        return hs300_period;
    }
    public void setHs300_period(long hs300_period) {
        this.hs300_period = hs300_period;
    }
}
