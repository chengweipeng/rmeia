package com.ism.rmeia.util;

public class MathUtil {

    public static float max(float ...args) {
        float maxValue= Float.MIN_VALUE;
        for(float arg:args){
            if(maxValue < arg){
                maxValue = arg;
            }
        }
        return maxValue;
    }
    public static float min(float ...args) {
        float maxValue= Float.MAX_VALUE;
        for(float arg:args){
            if(maxValue > arg){
                maxValue = arg;
            }
        }
        return maxValue;
    }
    
    public static float max(int ...args) {
        int maxValue= Integer.MIN_VALUE;
        for(int arg:args){
            if(maxValue < arg){
                maxValue = arg;
            }
        }
        return maxValue;
    }
    public static int min(int ...args) {
        int maxValue= Integer.MAX_VALUE;
        for(int arg:args){
            if(maxValue > arg){
                maxValue = arg;
            }
        }
        return maxValue;
    }
    
    //float a < float b;精度保留4位小数
    public static boolean lt(float a,float b){
        return a-b<-1E-4;
    }
    
    //float a > float b;精度保留4位小数
    public static boolean gt(float a,float b){
        return a-b>1E-4;
    }
    
    
    
    
    
 }
