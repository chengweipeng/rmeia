package com.ism.rmeia.day.strategy;

import com.ism.rmeia.bean.UserInfo;
import com.ism.rmeia.enumeration.TradingDayType;
import com.ism.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by wx on 2016/12/22.
 */
public class RmeiaStrategyFactory implements Serializable {
    private static Logger log = LoggerFactory.getLogger(RmeiaStrategyFactory.class);
    public static final String RMEIA_STRATEGY_DEFAULT_OPENDAY = "rmeia.strategy.default.openday";
    public static final String RMEIA_STRATEGY_DEFAULT_RUNDAY = "rmeia.strategy.default.runday";
    public static final String RMEIA_STRATEGY_DEFAULT_CLOSEDAY = "rmeia.strategy.default.closeday";

    protected static RmeiaStrategyFactory instance = new RmeiaStrategyFactory();

    Map<String, Class<?>> clsMap = new HashMap();

    Config cfg = new Config();

    public RmeiaStrategyFactory() {
        this(Config.getInstance());
    }
    public RmeiaStrategyFactory(Map<? extends Object,?extends Object> config) {
        cfg.putAll(config);
        loadDefaultStrategy();
    }

    public static RmeiaStrategyFactory getInstance(){
        return instance;
    }

    public void loadDefaultStrategy() {
        try {
            String opendayClsName = cfg.getString(RMEIA_STRATEGY_DEFAULT_OPENDAY, "com.ism.rmeia.day.strategy.OpenDayStrategy");
            String rundayClsName = cfg.getString(RMEIA_STRATEGY_DEFAULT_RUNDAY, "com.ism.rmeia.day.strategy.RunDayStrategy");
            String closedayClsName = cfg.getString(RMEIA_STRATEGY_DEFAULT_CLOSEDAY, "com.ism.rmeia.day.strategy.CloseDayStrategy");
            loadClass(opendayClsName,opendayClsName);
            loadClass(rundayClsName,rundayClsName);
            loadClass(closedayClsName,closedayClsName);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("load default day strategy failed {}",e.getMessage());
        }
    }

    public void loadClass(String clsName,String key) throws ClassNotFoundException {
        if(clsName != null && !clsName.isEmpty()){          
            Class<?> cls = Class.forName(clsName);
            clsMap.put(key, cls);
        }
    }
//    public <T> T newInstance(String fullClsName,
//                                        com.ism.rmeia.bean.UserInfo userInfo,
//                                        java.util.Collection<com.ism.rmeia.bean.IAStock> stocks,
//                                        java.util.Map<java.lang.String, com.ism.rmeia.bean.StockSignal> sigMap,
//                                        java.util.Map<java.lang.String, java.util.ArrayList<com.ism.market.KLine>> kLinesMap) {
//        try {
//            Class<?> cls = clsMap.get(fullClsName);
//            if (cls == null) {
//                log.error("class not found");
//                return null;
//            }
//            Constructor<?> ctor = (Constructor<?>) cls.getConstructor(UserInfo.class, Collection.class, Map.class, Map.class);
//            T strategy = (T) ctor.newInstance(userInfo, stocks, sigMap, kLinesMap);
//            return strategy;
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            log.error("ctor error={}", ex.getMessage());
//        }
//        return null;
//    }

    public <T> T newInstance(String fullClsName,
                             com.ism.rmeia.bean.UserInfo userInfo,
                             java.util.Collection<com.ism.rmeia.bean.IAStock> stocks,
                             java.util.Map<java.lang.String, com.ism.rmeia.bean.StockSignal> sigMap,
                             java.util.Map<java.lang.String, java.util.List<com.ism.market.KLine>> kLinesMap) {
        try {
            Class<?> cls = clsMap.get(fullClsName);
            if (cls == null) {
                log.error("class not found {}",fullClsName);
                return null;
            }
            Constructor<?> ctor = (Constructor<?>) cls.getConstructor(UserInfo.class, Collection.class, Map.class, Map.class);
            T strategy = (T) ctor.newInstance(userInfo, stocks, sigMap, kLinesMap);
            return strategy;
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("ctor error={}", ex.getMessage());
        }
        return null;
    }
}
