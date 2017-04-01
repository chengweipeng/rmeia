package com.ism.rmeia.task;

/**
 * Created by wx on 2016/5/5.
 */
public class StockPrice {
    String stock_code;
    String stock_time;
    String last_price;
    String open_price;
    String close_price;

    public String getStock_code() {
        return stock_code;
    }

    public void setStock_code(String stock_code) {
        this.stock_code = stock_code;
    }

    public String getStock_time() {
        return stock_time;
    }

    public void setStock_time(String stock_time) {
        this.stock_time = stock_time;
    }

    public String getLast_price() {
        return last_price;
    }

    public void setLast_price(String last_price) {
        this.last_price = last_price;
    }

    public String getOpen_price() {
        return open_price;
    }

    public void setOpen_price(String open_price) {
        this.open_price = open_price;
    }

    public String getClose_price() {
        return close_price;
    }

    public void setClose_price(String close_price) {
        this.close_price = close_price;
    }

    @Override
    public String toString() {
        return "StockPrice{" +
                "stock_code='" + stock_code + '\'' +
                ", stock_time='" + stock_time + '\'' +
                ", last_price='" + last_price + '\'' +
                ", open_price='" + open_price + '\'' +
                ", close_price='" + close_price + '\'' +
                '}';
    }
}
