package com.ism.rmeia.output;

import com.alibaba.fastjson.JSON;
import com.ism.rmeia.GlobalTemplateConfig;
import com.ism.rmeia.bean.InputIAInfo;
import com.ism.rmeia.bean.StockInstruction;
import com.ism.rmeia.bean.StockSignal;
import com.ism.rmeia.bean.UserStockInstruction;
import com.ism.rmeia.task.MessageConsume;
import com.ism.util.Config;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpStockInstructionProducerTest {

    @Before
    public void setUp() {
       /* Config cfg = Config.getInstance();
        try {
            cfg.loadSettings("cluster.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
        GlobalTemplateConfig globalConfig = new GlobalTemplateConfig(cfg);
        globalConfig.init();*/
    }

    @Test
    public void testProduce() {
        UserStockInstruction instruction = new UserStockInstruction();
        instruction.setIsm("1510080016025");
        instruction.setMsgId("adjust_test");
        instruction.setUid("9999");
        StockInstruction stock = new StockInstruction();
        stock.setBuy((byte) 1);
        stock.setEntrust_num(10000);
        stock.setLiveTime((short) 15);
        stock.setPrice(460);
        stock.setStockCode("601398");
        instruction.getOrders().add(stock);
        System.out.println(JSON.toJSONString(instruction));
        Map<String, StockSignal> sigMap = new HashMap<>();
        {
            StockSignal sig = new StockSignal();
            sig.lastupdate = (int) (System.currentTimeMillis() / 1000);
            sig.pred_15min = 4.5f;
            sig.pred_close = 5.0f;
            sig.pre_close = 4.0f;
            sig.sig_tm = sig.lastupdate - 10 * 60;
            sig.pred_close_std = 0.72;
            sigMap.put("601398", sig);
        }
        //mock IA 输入选项
        String json = "[{\"ismId\":\"1512026065566\",\"msgId\":\"RMEIA.20151204105828.108.111.99.97:5436.1792.92977\",\"afterBuildDay\":0,\"isCloseDay\":1,\"uid\":\"yhl123\",\"totalMoney\":42222.000000,\"totalFreeMoney\":0.000000,\"stock\":[{\"stockCode\":\"002233\",\"avg_cost\":0,\"stock_num\":0,\"available_num\":0,\"last_price\":1330,\"initBuildNum\":0},{\"stockCode\":\"600516\",\"avg_cost\":0,\"stock_num\":0,\"available_num\":0,\"last_price\":1385,\"initBuildNum\":0},{\"stockCode\":\"600552\",\"avg_cost\":0,\"stock_num\":0,\"available_num\":0,\"last_price\":2232,\"initBuildNum\":0},{\"stockCode\":\"002204\",\"avg_cost\":0,\"stock_num\":0,\"available_num\":0,\"last_price\":1073,\"initBuildNum\":0},{\"stockCode\":\"002459\",\"avg_cost\":0,\"stock_num\":0,\"available_num\":0,\"last_price\":1975,\"initBuildNum\":0},{\"stockCode\":\"002685\",\"avg_cost\":0,\"stock_num\":0,\"available_num\":0,\"last_price\":1180,\"initBuildNum\":0},{\"stockCode\":\"600114\",\"avg_cost\":0,\"stock_num\":0,\"available_num\":0,\"last_price\":1651,\"initBuildNum\":0},{\"stockCode\":\"002560\",\"avg_cost\":0,\"stock_num\":0,\"available_num\":0,\"last_price\":3779,\"initBuildNum\":0},{\"stockCode\":\"002692\",\"avg_cost\":0,\"stock_num\":0,\"available_num\":0,\"last_price\":1977,\"initBuildNum\":0},{\"stockCode\":\"002729\",\"avg_cost\":0,\"stock_num\":0,\"available_num\":0,\"last_price\":7069,\"initBuildNum\":0},{\"stockCode\":\"600537\",\"avg_cost\":0,\"stock_num\":0,\"available_num\":0,\"last_price\":1680,\"initBuildNum\":0},{\"stockCode\":\"000926\",\"avg_cost\":0,\"stock_num\":0,\"available_num\":0,\"last_price\":1664,\"initBuildNum\":0},{\"stockCode\":\"600053\",\"avg_cost\":0,\"stock_num\":0,\"available_num\":0,\"last_price\":5239,\"initBuildNum\":0},{\"stockCode\":\"600077\",\"avg_cost\":0,\"stock_num\":0,\"available_num\":0,\"last_price\":858,\"initBuildNum\":0},{\"stockCode\":\"600556\",\"avg_cost\":0,\"stock_num\":0,\"available_num\":0,\"last_price\":2507,\"initBuildNum\":0}]}]";
        List<InputIAInfo> l = JSON.parseArray(json, InputIAInfo.class);
        int i = 0;
        while (i++ < 2) {
            l.forEach(input -> {
                try {
                    new MessageConsume(input, sigMap).call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}