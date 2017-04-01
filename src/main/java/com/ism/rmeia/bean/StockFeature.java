package com.ism.rmeia.bean;

public class StockFeature {
    float curProfitRatio;// 当前收益率
    float expectProfitRatio;// 预期收益占比
    double expectCloseStd;//预测收盘的标准差
    double expectCloseSharpRatio;//预测收盘价夏普比率
    short liveTime;// 单位分钟

    // ism信息
    // IA信息
    // 持仓信息
    public float getCurProfit() {
        return curProfitRatio;
    }

    /**
     * 当前收益
     *
     * @param curProfit
     */
    public void setCurProfit(float curProfit) {
        this.curProfitRatio = curProfit;
    }

    public float getExpectProfit() {
        return expectProfitRatio;
    }

    /**
     * 预期收益
     *
     * @param expectProfit
     */
    public void setExpectProfit(float expectProfit) {
        this.expectProfitRatio = expectProfit;
    }

    /**
     * @return
     */
    public double getExpectCloseStd() {
        return expectCloseStd;
    }

    /**
     * 预测收盘价的标准差
     *
     * @param expectCloseStd
     */
    public void setExpectCloseStd(double expectCloseStd) {
        this.expectCloseStd = expectCloseStd;
    }

    public double getExpectCloseSharpRatio() {
        return expectCloseSharpRatio;
    }

    /**
     * 预测收盘价夏普比率
     *
     * @param expectCloseSharpRatio
     */
    public void setExpectCloseSharpRatio(double expectCloseSharpRatio) {
        this.expectCloseSharpRatio = expectCloseSharpRatio;
    }

    @Override
    public String toString() {
        return "StockFeature [curProfitRatio=" + curProfitRatio + ", expectProfitRatio=" + expectProfitRatio
                + ", expectCloseStd=" + expectCloseStd + ", expectCloseSharpRatio=" + expectCloseSharpRatio
                + ", liveTime=" + liveTime + "]";
    }
}
