package com.ism.rmeia.task;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ism.util.Config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wx on 2016/3/15.
 */
public class ConnectonMonitorTest {
    @Before
    public void setUp(){
    }

    @Test
    public void testGetSigMainAvailableConnString() throws ParseException {
        /*Config cfg = new Config();
        cfg.putProperty("sig.db.url", "jdbc:mysql://192.168.10.139:3306/SIG?characterEncoding=utf-8&autoReconnect=true&user=siguser&password=siguser");
        cfg.putProperty("sig.db.url.bak", "jdbc:mysql://192.168.10.138:3306/SIG?characterEncoding=utf-8&autoReconnect=true&user=datafeed&password=datafeed");
        ConnectonMonitor.getInstance().configure(cfg);
        ConnectonMonitor.DBCheckTask task = ConnectonMonitor.getInstance().getSigoutDbTask();
        task.setConfig(cfg);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        Date d = sdf.parse("20160315 11:00:00");
        task.setOperationTime(d.getTime());
        task.run();
        //if(TimeUtil.isTradeTime())
        String url = task.getAvailableConnUrl();
        Assert.assertEquals(url, cfg.getProperty("sig.db.url"));*/
    }
    @Test
    public void testGetSigBakAvailableConnString() throws ParseException {
        /*Config cfg = new Config();
        cfg.putProperty("sig.db.url", "jdbc:mysql://192.168.10.138:3306/SIG?characterEncoding=utf-8&autoReconnect=true&user=siguser&password=siguser");
        String bakUrlRight = "jdbc:mysql://192.168.10.139:3306/SIG?characterEncoding=utf-8&autoReconnect=true&user=siguser&password=siguser";
        cfg.putProperty("sig.db.url.bak", bakUrlRight);
        ConnectonMonitor.getInstance().configure(cfg);
        ConnectonMonitor.DBCheckTask task = ConnectonMonitor.getInstance().getSigoutDbTask();
        task.setConfig(cfg);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        Date d = sdf.parse("20160315 11:00:00");
        task.setOperationTime(d.getTime());
        task.run();
        //if(TimeUtil.isTradeTime())
        String url = task.getAvailableConnUrl();
        Assert.assertEquals(url, bakUrlRight);*/
    }
}
