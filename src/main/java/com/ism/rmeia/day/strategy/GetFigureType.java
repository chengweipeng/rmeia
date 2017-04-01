package com.ism.rmeia.day.strategy;

import com.ism.rmeia.enumeration.FigureType;

/**
 * 根据参数，计算图形A,B,C,....
 *
 * @author wx
 * @see FigureType
 */
public class GetFigureType {

    /**
     * <pre>
     * 最新价>=pred_15min>=pred_close，图形记为A 最新价
     * <= pred_15min<=pred_close，图形记为B pred_15min>=最新价>=pred_close，图形记为C
     * pred_15min<=最新价<=pred_close，图形记为D pred_15min>=pred_close>=最新价，图形记为E
     * pred_15min<=pred_close<=最新价，图形记为F
     *
     * @param curPrice
     *            当前价格
     * @param pred_15min
     *            15分钟的预测价格
     * @param pred_close
     *            收盘价预测
     * @return 返回 FigureType类型（A到F）
     */
    public static FigureType judgeFigureType(double curPrice, double pred_15min, double pred_close) {
        if (curPrice >= pred_15min && pred_15min >= pred_close) {
            return FigureType.A;
        }
        if (curPrice <= pred_15min && pred_15min <= pred_close) {
            return FigureType.B;
        }
        if (pred_15min >= curPrice && curPrice >= pred_close) {
            return FigureType.C;
        }
        if (pred_15min <= curPrice && curPrice <= pred_close) {
            return FigureType.D;
        }
        if (pred_15min >= pred_close && pred_close >= curPrice) {
            return FigureType.E;
        }
        if (pred_15min <= pred_close && pred_close <= curPrice) {
            return FigureType.F;
        }
        return null;
    }
}
