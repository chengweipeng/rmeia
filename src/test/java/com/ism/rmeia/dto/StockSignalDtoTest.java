package com.ism.rmeia.dto;

import java.sql.SQLException;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ism.db.DBHelper;
import com.ism.rmeia.bean.StockSignal;
import com.ism.util.Config;

public class StockSignalDtoTest {

    @Before
    public void setUp(){
        Config cfg=Config.getInstance();
        cfg.putProperty("master.db.url", "jdbc:mysql://192.168.10.139:3306/SIG?characterEncoding=utf-8&autoReconnect=true&user=siguser&password=siguser");
        cfg.putProperty("sig.db.url","jdbc:mysql://192.168.10.139:3306/SIG?characterEncoding=utf-8&autoReconnect=true&user=siguser&password=siguser");
        DBHelper.getInstance().configure(cfg);
    }
/*    @Test
    public void getSliceAllTest() {
        StockSignalDto dto = new StockSignalDto();
        try {
            Map<String,StockSignal> m = dto.getLatestSliceAll();
        } catch (SQLException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }*/
}
