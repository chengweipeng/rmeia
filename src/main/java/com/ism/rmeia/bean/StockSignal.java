package com.ism.rmeia.bean;

/**
 * SIG&E,连接数据库表SIGOUT
 *
 * @author wx
 */
public class StockSignal {
    public String stock_id;//股票代码
    public float pre_close;//昨收盘
    public float pred_15min;//15分钟预测价格
    public double pred_15min_std;//15分钟预测的标准差
    public float pred_close;//预测收盘
    public double pred_close_std;//预测价格标准差
    public float vpin;
    public double impact;
    public int sig_tm;//信号时间
    public int lastupdate;//更新时间

    @Override
    public int hashCode() {
        if (stock_id != null) {
            return stock_id.hashCode();
        }
        return 0;
    }

    @Override
    public String toString() {
        return "StockSignal [stock_id=" + stock_id + ", pre_close=" + pre_close + ", pred_15min=" + pred_15min
                + ", pred_15min_std=" + pred_15min_std + ", pred_close=" + pred_close + ", pred_close_std="
                + pred_close_std + ", vpin=" + vpin + ",impact=" + impact + ",sig_tm=" + sig_tm + ", lastupdate=" + lastupdate + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StockSignal other = (StockSignal) obj;
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
