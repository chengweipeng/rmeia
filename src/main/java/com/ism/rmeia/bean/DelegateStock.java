package com.ism.rmeia.bean;

import com.ism.enumeration.DELEGATE_METHOD;

/**
 * 用于计算实际需要卖出/买入的股数和股价
 *
 * @author wx
 */
public class DelegateStock {
    String stockCode;//股票代码
    int needOpenBuyNum;//建仓日有效，建仓日计算买入的股数
    int ownNum;// 实际持有数
    int availableNum;// 实际可用数
    int entrustNum;//实际委托数
    int delegateMethod;//委托方式：DELEGATE_METHOD 
    double avgPrice;// 平均成本
    double curPrice; //股票当前价
    double entrustPrice;//用于计算后的委托价格
    boolean bBuyDir;//买卖方向

    public DelegateStock() {

    }

    public DelegateStock(IAStock iaStock) {
        stockCode = iaStock.stockCode;
        ownNum = iaStock.ownNum;
        availableNum = iaStock.availableNum;
        curPrice = iaStock.curPrice;
        avgPrice = iaStock.getAvgPrice();
    }

    public String getStockCode() {
        return stockCode;

    }

    public int getNeedOpenBuyNum() {
        return needOpenBuyNum;
    }

    public void setNeedOpenBuyNum(int needOpenBuyNum) {
        this.needOpenBuyNum = needOpenBuyNum;
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

    public double getCurPrice() {
        return curPrice;
    }

    public void setCurPrice(double curPrice) {
        this.curPrice = curPrice;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public int getEntrustNum() {
        return entrustNum;
    }

    public void setEntrustNum(int entrustNum) {
        this.entrustNum = entrustNum;
    }

    public double getEntrustPrice() {
        return entrustPrice;
    }

    public void setEntrustPrice(double entrustPrice) {
        this.entrustPrice = entrustPrice;
    }

    /**
     * @return 返回委托方式
     * @see DELEGATE_METHOD
     */
    public int getDelegateMethod() {
        return delegateMethod;
    }

    public void setDelegateMethod(int delegateMethod) {
        this.delegateMethod = delegateMethod;
    }


    public boolean isBuyDir() {
        return bBuyDir;
    }

    public void setBuyDir(boolean isBuyDir) {
        this.bBuyDir = isBuyDir;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((stockCode == null) ? 0 : stockCode.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DelegateStock other = (DelegateStock) obj;
        if (stockCode == null) {
            if (other.stockCode != null)
                return false;
        } else if (!stockCode.equals(other.stockCode) && avgPrice == other.avgPrice && availableNum == other.availableNum)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "DelegateStock [stockCode=" + stockCode + ", needOpenBuyNum=" + needOpenBuyNum + ", ownNum=" + ownNum
                + ", availableNum=" + availableNum + ", entrustNum=" + entrustNum + ", delegateMethod=" + delegateMethod
                + ", avgPrice=" + avgPrice + ", curPrice=" + curPrice + ", entrustPrice=" + entrustPrice + ", bBuyDir="
                + bBuyDir + "]";
    }
}
