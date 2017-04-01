package com.ism.rmeia.enumeration;
/**
 * 对ISM进行分类
 * @author wx
 *
 * @since 1.1.6
 */
public enum IsmType {
    I,
    U;
    /**
     * 根据ISMID,判断ISM是否是U系列 
     * @param ismId
     * @return U返回true
     */
    public static boolean isUIsm(String ismId){
        return ismId.length()>=14 && ismId.charAt(6)=='9';
    }
    /**
     * 根据ISMID,判断ISM是否是U系列 
     * @param ismId
     * @return U返回true
     */
    public static IsmType getIsmType(String ismId){
        if( ismId.length()>=14 && ismId.charAt(6)=='9'){
            return U;
        }
        return I;
    }
}
