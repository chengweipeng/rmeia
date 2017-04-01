package com.ism.rmeia.rule;

import com.ism.rmeia.enumeration.FigureType;
import com.ism.rmeia.enumeration.TradingDayType;

public class FigureRule3 {

    /**
     * 获取卖出价格
     *
     * @param figureType
     * @param dayType
     * @param curPrice
     * @param pred_15min
     * @param pred_close
     * @return >0 实际价格 =0 市价委托 <0 不操作
     */
    public static double getSellPrice(FigureType figureType, TradingDayType dayType, double curPrice, double pred_15min,
                                      double pred_close) {
        double N = curPrice;
        double P = pred_15min;
        double C = pred_close;
        switch (figureType) {
            case A:
                switch (dayType) {
                    case R_DAY:
                        return (N + P) / 2;
                    case C_DAY:
                        return (N + P) / 2;
                    case O_DAY:
                        return -1;
                    default:
                        return -1;
                }
            case B:
                switch (dayType) {
                    case C_DAY:
                        return (C + P) / 2;
                    case R_DAY:
                        return (C + P) / 2;
                    default:
                        return -1;
                }
            case C:
                switch (dayType) {
                    case R_DAY:
                        return (N + P) / 2;
                    case C_DAY:
                        return (N + P) / 2;
                    default:
                        return -1;
                }
            case D:
                switch (dayType) {
                    case C_DAY:
                        return (N + C) / 2;
                    case R_DAY:
                        return (N + C) / 2;
                    default:
                        return -1;
                }
            case E:
                switch (dayType) {
                    case C_DAY:
                        return (C + P) / 2;
                    case R_DAY:
                        return (C + P) / 2;
                    default:
                        return -1;
                }
            case F:
                switch (dayType) {
                    case R_DAY:
                        return (N + C) / 2;
                    case C_DAY:
                        return (N + C) / 2;
                    default:
                        return -1;
                }
            default:
                return -1;
        }
    }

    
    /**
     * 获取需要买入设定的股价,单位元
     *@param figureType 图形类型
     *@param dayType 交易日类型 （建仓，调仓，平仓）
     *@param curPice 股票当前价格
     *@param pred_15min 预测15分钟后价格
     *@param pred_close 预测收盘价格
     * @return <0 表示不操作，>0 表示买入的股价 =0 市价委托
     */
    public static double getBuyPrice(FigureType figureType, TradingDayType dayType, double curPrice, double pred_15min,
                                     double pred_close) {
        double N = curPrice;
        double P = pred_15min;
        double C = pred_close;
        switch (figureType) {
            case A:
                switch (dayType) {
                    case O_DAY:
                        return (C + P) / 2;
                    default:
                        return -1;
                }
            case B:
                switch (dayType) {
                    case O_DAY:
                        return (N + P) / 2;
                    case R_DAY:
                        return (N + P) / 2;
                    default:
                        return -1;
                }
            case C:
                switch (dayType) {
                    case O_DAY:
                        return (C + N) / 2;
                    default:
                        return -1;
                }
            case D:
                switch (dayType) {
                    case O_DAY:
                        return (N + P) / 2;
                    case R_DAY:
                        return (N + P) / 2;
                    default:
                        return -1;
                }
            case E:
                switch (dayType) {
                    case O_DAY:
                        return (C + N) / 2;
                    case R_DAY:
                        return (C + N) / 2;
                    default:
                        return -1;
                }
            case F:
                switch (dayType) {
                    case O_DAY:
                        return (C + P) / 2;
                    default:
                        return -1;
                }
            default:
                return -1;
        }
    }
}
