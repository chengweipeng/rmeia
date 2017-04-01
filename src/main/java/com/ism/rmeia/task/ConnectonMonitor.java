package com.ism.rmeia.task;

import com.ism.db.DBHelper;
import com.ism.rmeia.dto.StockSignalDto;
import com.ism.util.Config;
import com.ism.util.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by wx on 2016/3/15.
 */
public class ConnectonMonitor {
    private final static Logger logger = LoggerFactory.getLogger(ConnectonMonitor.class);
    public static ConnectonMonitor me;
    Config config;
    int period = 60; //in seconds
    DBCheckTask sigoutDbTask ;
    ScheduledExecutorService schedule = Executors.newSingleThreadScheduledExecutor();
    long operationTime;
    protected ConnectonMonitor(Config config) {
        this.config = config;
        sigoutDbTask= new DBCheckTask();
        sigoutDbTask.setConfig(config);
        sigoutDbTask.setBackUrlProp("sig.db.url.bak");
        sigoutDbTask.setUrlProp("sig.db.url");
        operationTime = System.currentTimeMillis();
    }
    protected ConnectonMonitor() {
        this(new Config());
    }
    public static ConnectonMonitor getInstance() {
        if(me == null){
            synchronized (ConnectonMonitor.class) {
                if(me==null){
                    me = new ConnectonMonitor();
                }
            }
        }
        return me;
    }
    public  void configure(Config config){
        this.config = config;
        if(this.sigoutDbTask!=null) {
            sigoutDbTask.setConfig(config);
        }
    }

    public void start() {
        logger.info("ConnectonMonitor start...");
        if (schedule == null) {
            schedule = Executors.newSingleThreadScheduledExecutor();
        }
        try {
            schedule.scheduleAtFixedRate(sigoutDbTask, 1, period, TimeUnit.SECONDS);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * @return 返回可用的connection
     */
    public String getSigAvailableConnString(){
        if(sigoutDbTask == null){
            sigoutDbTask = new DBCheckTask();
            return "";
        }
        return sigoutDbTask.getAvailableConnUrl();
    }
    public long getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(long operationTime) {
        this.operationTime = operationTime;
        sigoutDbTask.setOperationTime(operationTime);
    }

    /**
     * visible for test
     */

    DBCheckTask getSigoutDbTask(){
        return sigoutDbTask;
    }

    public static class DBCheckTask implements Runnable {
        String urlProp;
        String backUrlProp;
        Config config;

        String yyyyMMdd;
        volatile String availableConnUrl;
        volatile boolean noAvailable;
        long operationTime = System.currentTimeMillis();


        public DBCheckTask() {
        }

        public DBCheckTask(String urlProp, String backUrlProp,Config config) {
            this.urlProp = urlProp;
            this.backUrlProp = backUrlProp;
            this.config = config;
        }

        private static boolean checkDBData(Connection con, String yyyyMMdd, long operationTime) throws SQLException {
            String sql = "select count(*) from SIG" + yyyyMMdd + " where sig_tm <= ? and sig_tm>?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setTimestamp(1,new Timestamp(operationTime));
            stmt.setTimestamp(2,new Timestamp(operationTime- StockSignalDto.SLICE_IN_SECONDS*1000L));
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                int count = rs.getInt(1);
                return count!=0;
            }
            return false;
        }
        public void run() {
            try {
                if(config == null){
                    return;
                }
                String mainConnString = config.getString(urlProp);
                if(availableConnUrl == null){
                    availableConnUrl = mainConnString;
                }

                if (!TimeUtil.isTradeTime(operationTime)) {
                    return;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                yyyyMMdd = sdf.format(new Date());

                if(hasData(mainConnString)){
                    this.availableConnUrl =  mainConnString;
                    return;
                }
                String bak = config.getString(backUrlProp);
                String hasDataString = selectBackConnectionString(bak);
                if(StringUtils.isNotEmpty(hasDataString)){
                    availableConnUrl = hasDataString;
                    logger.info("[CONNECTION]url changed,url={}",availableConnUrl);
                    return;
                }
                logger.info("[CONNECTION] all url invalid,no data.");
                //到这里，所有的connnsting都无效；那么设置为主
                availableConnUrl = mainConnString;
                noAvailable = true;
                logger.info("[CONNECTION] checked,url={}",availableConnUrl);
            } catch (Throwable t) {
                t.printStackTrace();
                logger.info("[CONNECTION] check exeception = {}",t.getCause());
            }
        }

        public boolean isNoAvailable(){
            return noAvailable;
        }

        public String getAvailableConnUrl() {
            return availableConnUrl;
        }

        public void setConfig(Config config) {
            this.config = config;
        }

        public void setBackUrlProp(String backUrlProp) {
            this.backUrlProp = backUrlProp;
        }

        public void setOperationTime(long operationTime){
            this.operationTime = operationTime;
        }

        public void setUrlProp(String urlProp) {
            this.urlProp = urlProp;
        }

        /**
         * get db connection from bak( according the order of occur in properties,and with  seperator ";")
         *
         * @return null if all not available
         */
        private String selectBackConnectionString(String bak) {
            String[] conn_strings = StringUtils.split(bak, ";");
            for (String conn_str : conn_strings) {
                if (hasData(conn_str)) return conn_str;
            }
            return "";
        }

        private boolean hasData(String conn_str) {
            Connection con = null;
            try {
                con = DBHelper.newConnection(conn_str);
                if(con==null){
                    return false;
                }
                boolean hasData = checkDBData(con,yyyyMMdd,operationTime);
                return hasData;
            } catch (SQLException e) {
                e.printStackTrace();
                logger.warn("check Connection SQLError",e.getCause());
            } finally {
                try {
                  if(con!=null) {
                      con.close();
                  }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    }
}
