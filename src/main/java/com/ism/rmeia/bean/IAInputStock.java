package com.ism.rmeia.bean;

import java.io.Serializable;

/**
 * IA 输入给RMEIA的数据
 * @author wx
 *
 */
public  class IAInputStock implements Serializable{
    String stockCode;
    float avg_cost;
    int stock_num;
    int available_num;
    int last_price;//最新价格，单位分
    int initBuildNum;

    public String getStockCode() {
        return stockCode;
    }

    public float getAvg_cost() {
        return avg_cost;
    }

    public int getStock_num() {
        return stock_num;
    }

    public int getAvailable_num() {
        return available_num;
    }

    /**
     * 单位 分
     * 
     * @return 返回以分为单位的价格
     */
    public int getLast_price() {
        return last_price;
    }

    public int getInitBuildNum() {
        return initBuildNum;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public void setAvg_cost(float avg_cost) {
        this.avg_cost = avg_cost;
    }

    public void setStock_num(int stock_num) {
        this.stock_num = stock_num;
    }

    public void setAvailable_num(int available_num) {
        this.available_num = available_num;
    }

    public void setLast_price(int last_price) {
        this.last_price = last_price;
    }

    public void setInitBuildNum(int initBuildNum) {
        this.initBuildNum = initBuildNum;
    }

    @Override
    public String toString() {
        return "IAInputStock [stockCode=" + stockCode + ", avg_cost="
                + avg_cost + ", stock_num=" + stock_num + ", available_num="
                + available_num + ", last_price=" + last_price
                + ", initBuildNum=" + initBuildNum + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((stockCode == null) ? 0 : stockCode.hashCode());
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
        IAInputStock other = (IAInputStock) obj;
        if (stockCode == null) {
            if (other.stockCode != null)
                return false;
        } else if (!stockCode.equals(other.stockCode))
            return false;
        return true;
    }
    
}