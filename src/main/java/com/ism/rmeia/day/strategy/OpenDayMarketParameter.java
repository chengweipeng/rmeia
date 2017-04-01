package com.ism.rmeia.day.strategy;

/**
 * 建仓日涉及到的行情参数
 */
public class OpenDayMarketParameter {

    public float close_4;
    public float close_3;
    public float close_2;
    public float close_1;
    public float close_0;
    
    public float ma_4;
    public float ma_0;
    
    public float dif_4;
    public float dif_3;
    public float dif_2;
    public float dif_1;
    public float dif_0;
    
    public float dea_4;
    public float dea_3;
    public float dea_2;
    public float dea_1;
    public float dea_0;
    
    public long ts;//参数的时间 timestamp，单位seconds
   
    public OpenDayMarketParameter(){
    }
    @Override
    public String toString() {
        return "{" +
            "close_4=" + close_4 +
            ", close_3=" + close_3 +
            ", close_2=" + close_2 +
            ", close_1=" + close_1 +
            ", close_0=" + close_0 +
            ", ma_4=" + ma_4 +
            ", ma_0=" + ma_0 +
            ", dif_4=" + dif_4 +
            ", dif_3=" + dif_3 +
            ", dif_2=" + dif_2 +
            ", dif_1=" + dif_1 +
            ", dif_0=" + dif_0 +
            ", dea_4=" + dea_4 +
            ", dea_3=" + dea_3 +
            ", dea_2=" + dea_2 +
            ", dea_1=" + dea_1 +
            ", dea_0=" + dea_0 +
            ", ts=" + ts +
            '}';
    }
}
