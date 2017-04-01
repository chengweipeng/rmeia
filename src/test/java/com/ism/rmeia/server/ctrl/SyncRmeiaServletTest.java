package com.ism.rmeia.server.ctrl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;

import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.alibaba.fastjson.JSON;
import com.ism.rmeia.bean.UserStockInstruction;
import com.ism.rmeia.util.Enviroment;
import com.ism.util.Config;

import junit.framework.Assert;

public class SyncRmeiaServletTest {
    @Before
    public void setUp() throws IOException {
        Config.getInstance().loadSettings("cluster.properties");
        Enviroment.config(Config.getInstance());
    }

    @Test
    public void testDoPostSell() throws ServletException, IOException {
        // <1>卖出
        {
            String json = "[{\"request_tm\":1482475841,\"ia_status\":\"0\",\"order_id\":\"123456\",\"ismId\":\"123456\",\"msgId\":\"11\",\"afterBuildDay\":0,\"isCloseDay\":1,\"uid\":\"1111\",\"totalMoney\":100000,\"totalFreeMoney\":100000,\"totalMacketValue\":0,\"stock\":[{\"available_num\":0,\"initBuildNum\":100,\"avg_cost\":1000,\"last_price\":1000,\"stock_num\":0,\"stockCode\":600000,\"stock_id\":600000}],\"kline\":[{\"MA\":1,\"CLOSE\":0,\"DIF\":0,\"DEA\":0},{\"MA\":0,\"CLOSE\":1,\"DIF\":0,\"DEA\":0},{\"MA\":0,\"CLOSE\":0,\"DIF\":0,\"DEA\":0},{\"MA\":1,\"CLOSE\":9,\"DIF\":0,\"DEA\":0},{\"MA\":0,\"CLOSE\":10,\"DIF\":0,\"DEA\":0},{\"MA\":0,\"CLOSE\":0,\"DIF\":0,\"DEA\":0}]}]";
            byte[] retBytes = mockOut(new StringReader(json));
            String retJson = new String(retBytes);
            Assert.assertTrue(!retJson.isEmpty());
        }
    }
    
    @Test
    public void testDoPostBuy() throws ServletException, IOException {
        // <2>买入
        {
            String json = "[{\"request_tm\":1482475841,\"ia_status\":\"0\",\"order_id\":\"123456\",\"ismId\":\"123456\",\"msgId\":\"11\",\"afterBuildDay\":0,\"isCloseDay\":0,\"uid\":\"1111\",\"totalMoney\":100000,\"totalFreeMoney\":100000,\"totalMacketValue\":0,\"stock\":[{\"available_num\":100,\"initBuildNum\":0,\"avg_cost\":1000,\"last_price\":1000,\"stock_num\":100,\"stockCode\":600000,\"stock_id\":600000}],\"kline\":[{\"MA\":1,\"CLOSE\":0,\"DIF\":0,\"DEA\":0},{\"MA\":0,\"CLOSE\":1,\"DIF\":0,\"DEA\":0},{\"MA\":0,\"CLOSE\":0,\"DIF\":0,\"DEA\":0},{\"MA\":1,\"CLOSE\":9,\"DIF\":0,\"DEA\":0},{\"MA\":0,\"CLOSE\":10,\"DIF\":0,\"DEA\":0},{\"MA\":0,\"CLOSE\":0,\"DIF\":0,\"DEA\":0}]}]";
            byte[] retBytes = mockOut(new StringReader(json));
            String retJson = new String(retBytes);
            System.out.println(retJson);
            Assert.assertTrue(!retJson.isEmpty());
        }
    }

    @Test
    public void testDoPostInvalidParameter() {
        String json = "[]";
        try {
            byte[] retBytes = mockOutFail(new StringReader(json));
            Assert.assertFalse(new String(retBytes).contains("SEL"));
        } catch (Throwable t) {
        }
    }

    @Test
    public void FigureB_buy_price() {
        //String json = "[{\"request_tm\":1451009880,\"ia_status\":\"0\",\"order_id\":\"10\",\"ismId\":\"6001\",\"msgId\":\"6001\",\"afterBuildDay\":1,\"isCloseDay\":0,\"uid\":\"10\",\"totalMoney\":650000,\"totalFreeMoney\":30000,\"stock\":[{\"available_num\":800,\"initBuildNum\":0,\"avg_cost\":1850,\"last_price\":1832,\"stock_num\":800,\"stockCode\":\"600100\",\"stock_id\":\"600100\",\"pre_close\":17.8,\"pred_15min\":18.5,\"pred_close\":18.8,\"pred_close_std\":0.0156,\"sig_tm\":1000,\"lastupdate\":1000}]}]";
        String json = "[{\"request_tm\":1482475841,\"ia_status\":\"0\",\"order_id\":\"123456\",\"ismId\":\"123456\",\"msgId\":\"11\",\"afterBuildDay\":0,\"isCloseDay\":1,\"uid\":\"1111\",\"totalMoney\":100000,\"totalFreeMoney\":100000,\"totalMacketValue\":0,\"stock\":[{\"available_num\":100,\"initBuildNum\":0,\"avg_cost\":1000,\"last_price\":1000,\"stock_num\":100,\"stockCode\":600000,\"stock_id\":600000}],\"kline\":[{\"MA\":1,\"CLOSE\":0,\"DIF\":0,\"DEA\":0},{\"MA\":0,\"CLOSE\":1,\"DIF\":0,\"DEA\":0},{\"MA\":0,\"CLOSE\":0,\"DIF\":0,\"DEA\":0},{\"MA\":1,\"CLOSE\":9,\"DIF\":0,\"DEA\":0},{\"MA\":0,\"CLOSE\":10,\"DIF\":0,\"DEA\":0},{\"MA\":0,\"CLOSE\":0,\"DIF\":0,\"DEA\":0}]}]";
        byte[] retBytes = null;
        try {
            retBytes = mockOut(new StringReader(json));
        } catch (IOException | ServletException e) {
            Assert.fail();
            e.printStackTrace();
        }
        String retJson = new String(retBytes);
        System.err.println(retJson);
        Assert.assertTrue(retJson.contains("\"buy\":0"));
    }

    private byte[] mockOut(Reader reader) throws IOException, ServletException {
        HttpServletRequest reqMock = (HttpServletRequest) Mockito.mock(HttpServletRequest.class);
        HttpServletResponse respMock = (HttpServletResponse) Mockito.mock(HttpServletResponse.class);

        Mockito.when(reqMock.getInputStream()).thenReturn(new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return true;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }

            @Override
            public int read() throws IOException {
                return reader.read();
            }
        });
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Mockito.when(respMock.getOutputStream()).thenReturn(new ServletOutputStream() {

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
            }

            @Override
            public void write(int b) throws IOException {
                bos.write(b);
            }
        });

        Mockito.when(respMock.getWriter()).thenReturn(new PrintWriter(bos));

        SyncRmeiaServlet servlet = new SyncRmeiaServlet();
        servlet.doPost(reqMock, respMock);
        return bos.toByteArray();
    }

    private byte[] mockOutFail(Reader reader) throws IOException, ServletException {
        HttpServletRequest reqMock = (HttpServletRequest) Mockito.mock(HttpServletRequest.class);
        HttpServletResponse respMock = (HttpServletResponse) Mockito.mock(HttpServletResponse.class);

        Mockito.when(reqMock.getInputStream()).thenReturn(new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return true;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }

            @Override
            public int read() throws IOException {
                return reader.read();
            }
        });
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Mockito.when(respMock.getOutputStream()).thenReturn(new ServletOutputStream() {

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
            }

            @Override
            public void write(int b) throws IOException {
                bos.write(b);
            }
        });

        Mockito.when(respMock.getWriter()).thenReturn(new PrintWriter(bos));

        SyncRmeiaServlet servlet = new SyncRmeiaServlet();
        servlet.doPost(reqMock, respMock);
        return bos.toByteArray();
    }

}
