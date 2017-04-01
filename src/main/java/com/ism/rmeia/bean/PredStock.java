package com.ism.rmeia.bean;
/**
 * 
 * @author wx
 */
public class PredStock{
    public String stock_id; //股票代码
    public double pre_close;//前一个交易日收盘价
    public double pred[];//预测值
    public double std[];//标准差
    public int sig_tm;//预测时间
    public PredStock(){
        stock_id="";
        pre_close=0;
        pred = new double[14];
        std = new double[14];
        sig_tm=0;
    }
    public String getStock_id() {
        return stock_id;
    }
    public void setStock_id(String stock_id) {
        this.stock_id = stock_id;
    }
    public double getPre_close() {
        return pre_close;
    }
    public void setPre_close(double pre_close) {
        this.pre_close = pre_close;
    }
    public double[] getPred() {
        return pred;
    }
    public void setPred(double[] pred) {
        this.pred = pred;
    }
    public double[] getStd() {
        return std;
    }
    public void setStd(double[] std) {
        this.std = std;
    }
    public int getSig_tm() {
        return sig_tm;
    }
    public void setSig_tm(int sig_tm) {
        this.sig_tm = sig_tm;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + sig_tm;
        result = prime * result + ((stock_id == null) ? 0 : stock_id.hashCode());
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
        PredStock other = (PredStock) obj;
        if (sig_tm != other.sig_tm)
            return false;
        if (stock_id == null) {
            if (other.stock_id != null)
                return false;
        } else if (!stock_id.equals(other.stock_id))
            return false;
        return true;
    }
    
}
