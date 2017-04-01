package com.ism.rmeia.dto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.ism.db.DBHelper;
import com.ism.rmeia.bean.PredStock;
import com.ism.rmeia.bean.StockSignal;
import com.ism.util.DBUtil;
import com.ism.util.TimeUtil;
/**
 * 获取股票预测数据的DTO
 * @author wx
 *
 */
public class PredStockDto {
    private final static Logger logger = LoggerFactory.getLogger(PredStockDto.class);
    public  final static int SLICE_IN_SECONDS = 15 * 60;
   
    int sliceInsecs;
    public PredStockDto() {
        sliceInsecs=SLICE_IN_SECONDS;
    }

    /**
     * 获取当前切片周期的所有数据
     *
     * @return KEY为股票代码, Value 为元组合
     * @throws SQLException
     */
    public Map<String, PredStock> getSliceAll(Connection con, long timestampInMs) throws SQLException {
        Preconditions.checkNotNull(con);
        Map<String, PredStock> retMap = new HashMap<>();
        // 计算时间片
        if (TimeUtil.beforeOpen(timestampInMs) || TimeUtil.afterClose(timestampInMs)) {
            return retMap;
        }
        String sql = "select * from sig_conti_out where sig_conti_tm <= ? and sig_conti_tm>?";

        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(sql);
            stmt.setTimestamp(1, new Timestamp(timestampInMs));
            stmt.setTimestamp(2, new Timestamp(timestampInMs - this.sliceInsecs * 1000));
            logger.debug("sql={}", stmt);
            ResultSet rs = stmt.executeQuery();
            while (rs != null && rs.next()) {
                String stock_id = rs.getString("stock_id");
                double pre_close = rs.getDouble("pre_close");
                Timestamp sig_tm = rs.getTimestamp("sig_tm");
                //Timestamp lastupdate = rs.getTimestamp("lastupdate");
                PredStock sig = new PredStock();
                sig.stock_id = stock_id;
                sig.pre_close = pre_close;
                for(int k=0;k<14;++k){
                    double pred = rs.getDouble("pred_"+k);
                    double std = rs.getDouble("std_"+k);
                    sig.getPred()[k]=pred;
                    sig.getStd()[k]=std;
                }
                sig.sig_tm = (int) (sig_tm.getTime() / 1000);
                retMap.put(stock_id, sig);
            }
        } finally {
            DBUtil.closeQuiety(stmt);
        }
        return retMap;
    }

    /**
     * 获取当前切片周期的所有数据
     * 
     * @param con
     *            数据库连接
     * @return KEY为股票代码, Value 为元组合
     * @throws SQLException
     */
    public Map<String, PredStock> getLatestSliceAll(Connection con) throws SQLException {
        return getSliceAll(con, System.currentTimeMillis());
    }

    /**
     * 指定某个时间的，获取该时间切片周期的所有数据
     * 
     * @param timestampInMs
     *            指定某个时间
     * @return KEY为股票代码, Value 为元组合
     * @throws SQLException
     */
    public Map<String, PredStock> getSliceAll(long timestampInMs) throws SQLException {
        Connection con = DBHelper.getInstance().getConnection("pred.db.url");
        try {
            return getSliceAll(con,timestampInMs);
        } finally {
            DBUtil.closeQuiety(con);
        }
    }

    /**
     * 获取当前切片周期的所有数据
     *
     * @return KEY为股票代码, Value 为元组合
     * @throws SQLException
     */
    public Map<String, PredStock> getLatestSliceAll() throws SQLException {
        Connection con = DBHelper.getInstance().getConnection("pred.db.url");
        try {
            return getLatestSliceAll(con);
        } finally {
            DBUtil.closeQuiety(con);
        }
    }

    public boolean insert(StockSignal sig) throws SQLException {
        throw new UnsupportedOperationException("never use yet.");
    }

    public int update(StockSignal sig) throws SQLException {
        throw new UnsupportedOperationException("never use yet.");
    }

    public int getSliceInsecs() {
        return sliceInsecs;
    }

    public void setSliceInsecs(int sliceInsecs) {
        this.sliceInsecs = sliceInsecs;
    }
}
