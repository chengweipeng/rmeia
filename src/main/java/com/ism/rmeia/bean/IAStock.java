package com.ism.rmeia.bean;

public class IAStock {
    String stockCode;//股票代码
    int needOpenBuyNum;//建仓日有效，建仓日需要买入的股数
    int ownNum;// 实际持有数
    int availableNum;// 实际可用数
    private double avgPrice;// 平均成本
    double curPrice;

    public IAStock() {
    }
    public static IAStock create() {
        return new IAStock();
    }

    /**
     * @return 返回股票价格（元）
     */
    public double getCurPrice() {
        return curPrice;
    }

    /**
     * @param curPrice 当前股票价格，单位元
     */
    public void setCurPrice(double curPrice) {
        this.curPrice = curPrice;
    }

    @Override
    public IAStock clone() {
        IAStock stockHolder = new IAStock();
        stockHolder.stockCode = stockCode;
        stockHolder.needOpenBuyNum = needOpenBuyNum;
        stockHolder.ownNum = ownNum;
        stockHolder.availableNum = availableNum;
        stockHolder.avgPrice = avgPrice;
        stockHolder.curPrice = curPrice;
        return stockHolder;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public int getOwnNum() {
        return ownNum;
    }

    public void setOwnNum(int ownNum) {
        this.ownNum = ownNum;
    }

    public int getAvailableNum() {
        return availableNum;
    }

    public void setAvailableNum(int availableNum) {
        this.availableNum = availableNum;
    }

    public double getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(double avgPrice) {
        this.avgPrice = avgPrice;
    }

    public int getNeedOpenBuyNum() {
        return needOpenBuyNum;
    }

    public void setNeedOpenBuyNum(int needOpenBuyNum) {
        this.needOpenBuyNum = needOpenBuyNum;
    }

    @Override
    public String toString() {
        return "IAStock [stockCode=" + stockCode + ", needOpenBuyNum=" + needOpenBuyNum + ", ownNum=" + ownNum
                + ", availableNum=" + availableNum + ", avgPrice=" + avgPrice + ", curPrice=" + curPrice + "]";
    }
}
