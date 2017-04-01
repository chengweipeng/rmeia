package com.ism.rmeia.bean;

/**
 * 返回给IA的交易指令
 *
 * @author wx
 */
public class StockInstruction {
    String stockCode;
    int entrust_num;
    int price;//单位分
    byte buy;
    byte method;
    //单位分钟
    short liveTime;

    /**
     * 指令存续期
     *
     * @return
     */
    public short getLiveTime() {
        return liveTime;
    }

    /**
     * 单位分钟
     *
     * @param liveTime 生存周期
     */
    public void setLiveTime(short liveTime) {
        this.liveTime = liveTime;
    }

    /**
     * 
     * @return 股票代码
     */
    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    /**
     * 
     * @return 委托数量
     */
    public int getEntrust_num() {
        return entrust_num;
    }

    public void setEntrust_num(int entrust_num) {
        this.entrust_num = entrust_num;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    /**
     * 
     * @return 买卖方向（0:卖，1：买）
     */
    public byte getBuy() {
        return buy;
    }

    public void setBuy(byte buy) {
        this.buy = buy;
    }
    /**
     * 
     * @return 委托方式：0:限价/1:市价
     */
    public byte getMethod() {
        return method;
    }

    public void setMethod(byte method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "StockInstruction [stockCode=" + stockCode + ", entrust_num=" + entrust_num + ", price=" + price
                + ", buy=" + buy + ", method=" + method + ", liveTime=" + liveTime + "]";
    }

}
