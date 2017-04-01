package com.ism.rmeia.util;

public class FeeUtil {
    
    public static float getDefaultFee(double tradeMoney){
        return (float) Math.max(tradeMoney*0.003,5.04);
    }
}
