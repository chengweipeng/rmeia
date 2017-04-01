package com.ism.rmeia.rule;

import com.ism.rmeia.day.strategy.CloseDayMarketParameter;
import com.ism.rmeia.day.strategy.RunDayMarketParameter;
import com.ism.rmeia.util.MathUtil;

/**
 * 
 * @author wx
 *
 */
public class SellRule5 {
    
    public static boolean sell_old(CloseDayMarketParameter param){
        if(param.close_4 > param.ma_4 && param.close_0<param.close_4 && param.close_0<param.close_3 &&
           param.close_0<param.close_2 && param.close_0<param.close_1 ){
            if(param.dif_4>0 && param.dea_4>0 && param.dif_3 >0 &&param.dea_3>0 && 
                    param.dif_4>param.dea_4 && param.dif_3 < param.dea_3 && param.dif_2 <param.dea_2 && param.dif_1 <param.dea_1 &&
                    (param.dif_0-param.dea_0) < MathUtil.min(param.dif_3-param.dea_3,param.dif_2 - param.dea_2,param.dif_1-param.dea_1))
            {
                return true;
            }
        }
        return false;
    }
    
    public static boolean sell(CloseDayMarketParameter param) {
        if (MathUtil.gt(param.close_4, param.ma_4) && MathUtil.lt(param.close_0, param.close_4)
                && MathUtil.lt(param.close_0, param.close_3) && MathUtil.lt(param.close_0, param.close_2)
                && MathUtil.lt(param.close_0, param.close_1)) {
            if (param.dif_4 > 1E-4 && param.dea_4 > 1E-4 && param.dif_3 > 1E-4 && param.dea_3 > 1E-4
                    && MathUtil.gt(param.dif_4, param.dea_4) && MathUtil.lt(param.dif_3, param.dea_3)
                    && MathUtil.lt(param.dif_2, param.dea_2) && MathUtil.lt(param.dif_1, param.dea_1)
                    && (param.dif_0 - param.dea_0) < MathUtil.min(param.dif_3 - param.dea_3, param.dif_2 - param.dea_2,
                            param.dif_1 - param.dea_1)) {
                return true;
            }
        }
        return false;
    }
    
    
    
    public static boolean sell_old(RunDayMarketParameter param){
        if(param.close_4 > param.ma_4 && param.close_0<param.close_4 && param.close_0<param.close_3 &&
           param.close_0<param.close_2 && param.close_0<param.close_1 ){
            if(param.dif_4>0 && param.dea_4>0 && param.dif_3 >0 &&param.dea_3>0 && 
                    param.dif_4>param.dea_4 && param.dif_3 < param.dea_3 && param.dif_2 <param.dea_2 && param.dif_1 <param.dea_1 &&
                    (param.dif_0-param.dea_0) < MathUtil.min(param.dif_3-param.dea_3,param.dif_2 - param.dea_2,param.dif_1-param.dea_1))
            {
                return true;
            }
        }
        return false;
    }
    
    public static boolean sell(RunDayMarketParameter param) {
        if (MathUtil.gt(param.close_4, param.ma_4) && MathUtil.lt(param.close_0, param.close_4)
                && MathUtil.lt(param.close_0, param.close_3) && MathUtil.lt(param.close_0, param.close_2)
                && MathUtil.lt(param.close_0, param.close_1)) {
            if (param.dif_4 > 1E-4 && param.dea_4 > 1E-4 && param.dif_3 > 1E-4 && param.dea_3 > 1E-4
                    && MathUtil.gt(param.dif_4, param.dea_4) && MathUtil.lt(param.dif_3, param.dea_3)
                    && MathUtil.lt(param.dif_2, param.dea_2) && MathUtil.lt(param.dif_1, param.dea_1)
                    && (param.dif_0 - param.dea_0) < MathUtil.min(param.dif_3 - param.dea_3, param.dif_2 - param.dea_2,
                            param.dif_1 - param.dea_1)) {
                return true;
            }
        }
        return false;
    }
    
    
    
}
