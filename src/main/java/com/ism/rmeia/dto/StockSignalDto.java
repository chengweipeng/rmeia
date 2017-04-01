package com.ism.rmeia.dto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.ism.db.DBHelper;
import com.ism.rmeia.bean.StockSignal;
import com.ism.rmeia.task.ConnectonMonitor;
import com.ism.util.Config;
import com.ism.util.DBUtil;
import com.ism.util.TimeUtil;

public class StockSignalDto {
    private final static Logger logger = LoggerFactory.getLogger(StockSignalDto.class);
    public  final static int SLICE_IN_SECONDS = 15 * 60;
    int sliceInsecs;
    public StockSignalDto() {
        this.sliceInsecs =  SLICE_IN_SECONDS;
    }

    /**
     * 获取当前切片周期的所有数据
     *
     * @return KEY为股票代码, Value 为元组合
     * @throws SQLException
     */
    public Map<String, StockSignal> getSliceAll(Connection con, long timestampInMs) throws SQLException {
        Preconditions.checkNotNull(con);
        Map<String, StockSignal> retMap = new HashMap<String, StockSignal>();
        // 计算时间片
        if (TimeUtil.beforeOpen(timestampInMs) || TimeUtil.afterClose(timestampInMs)) {
            return retMap;
        }
        Date d = new Date(timestampInMs);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String yyyyMMdd = sdf.format(d);
        String sql = getSql(yyyyMMdd);
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(sql);
            stmt.setTimestamp(1, new Timestamp(timestampInMs));
            stmt.setTimestamp(2, new Timestamp(timestampInMs - SLICE_IN_SECONDS * 1000));
            logger.debug("sql={}", stmt);
            ResultSet rs = stmt.executeQuery();
            while (rs != null && rs.next()) {
                String stock_id = rs.getString("stock_id");
                float pre_close = rs.getFloat("pre_close");
                float pred_15min = rs.getFloat("pred_15min");
                double pred_15min_std = rs.getDouble("pred_15min_std");
                float pred_close = rs.getFloat("pred_close");
                double pred_close_std = rs.getDouble("pred_close_std");
                float vpin = rs.getFloat("vpin");
                double impact = rs.getDouble("impact");
                Timestamp sig_tm = rs.getTimestamp("sig_tm");
                Timestamp lastupdate = rs.getTimestamp("lastupdate");//new Timestamp(sig_tm.getTime());
                StockSignal sig = new StockSignal();
                sig.stock_id = stock_id;
                sig.pre_close = pre_close;
                sig.pred_15min = pred_15min;
                sig.pred_15min_std = pred_15min_std;
                sig.pred_close = pred_close;
                sig.pred_close_std = pred_close_std;
                sig.vpin = vpin;
                sig.impact = impact;
                sig.sig_tm = (int) (sig_tm.getTime() / 1000);
                sig.lastupdate = (int) (lastupdate.getTime() / 1000);
                //logger.debug("sig={}", sig);
                retMap.put(stock_id, sig);
            }
        } finally {
            DBUtil.closeQuiety(stmt);
        }
        return retMap;
    }

    private String getSql(String yyyyMMdd) {
        return "select * from SIG"+yyyyMMdd+" where sig_tm <= ? and sig_tm>?";
    }

    /**
     * 获取当前切片周期的所有数据
     * 
     * @param con
     *            数据库连接
     * @return KEY为股票代码, Value 为元组合
     * @throws SQLException
     */
    public Map<String, StockSignal> getLatestSliceAll(Connection con) throws SQLException {
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
    public Map<String, StockSignal> getSliceAll(long timestampInMs) throws SQLException {
        Connection con = DBHelper.newConnection(getCurrentConnectonString());
        try {
            if(con ==null) return Collections.emptyMap();
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
	public Map<String, StockSignal> getLatestSliceAll() throws SQLException {
		String conn_str = getCurrentConnectonString();
		logger.info("sql SIG source:{}",conn_str);
		Connection con = DBHelper.newConnection(conn_str);
		try {
			Map<String, StockSignal> retMap = getLatestSliceAll(con);
			if (retMap == null || retMap.isEmpty()) {
				return Collections.emptyMap();
			} else {
				return retMap;
			}
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

    public  String getCurrentConnectonString(){
        String str=  ConnectonMonitor.getInstance().getSigAvailableConnString();
        if(str == ""||str==null){
            return Config.getInstance().getString("sig.db.url");
        }
        return str;
    }
}
