package com.ism.rmeia.server.ctrl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;

import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.ism.rmeia.GlobalTemplateConfig;
import com.ism.util.Config;

public class RmeiaGetIAServletTest {
    @Before
    public void setUp() {
        Config cfg = Config.getInstance();   
        try {
           cfg.loadSettings("cluster.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
        GlobalTemplateConfig global = new GlobalTemplateConfig(cfg);
        global.init();
        global.start();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testGenReturnBackURL() throws MalformedURLException {
        RmeiaGetIAServlet servlet = new RmeiaGetIAServlet();
        String newUrl = servlet.getBackUrl("http://10.0.150.16:6060/rmeia?backurl=10.0.150.17:9999/ia-boot/adjust?","http://localhost:8080/adjust?");
        //System.out.println(newUrl);
        assert(newUrl.equals("http://10.0.150.17:9999/ia-boot/adjust?")||newUrl.equals("http://localhost:8080/adjust?"));
    }

    @Test
    public void testDoPost() throws IOException, ServletException {
        HttpServletRequest reqMock = (HttpServletRequest) Mockito.mock(HttpServletRequest.class);
        HttpServletResponse respMock = (HttpServletResponse) Mockito.mock(HttpServletResponse.class);
        InputStreamReader reader = new InputStreamReader(
                RmeiaGetIAServletTest.class.getClassLoader().getResourceAsStream("request.json"), "UTF-8");
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
        Mockito.when(respMock.getWriter()).thenReturn( new PrintWriter(bos));
        RmeiaGetIAServlet servlet = new RmeiaGetIAServlet();
        servlet.doPost(reqMock, respMock);
        
        byte[] bytes= bos.toByteArray();
        Assert.assertTrue(bytes!=null);
        Assert.assertEquals("{\"msg\":\"HTTP OK\",\"status\":0}",new String(bytes));
        System.err.println(new String(bytes));
    }
}
