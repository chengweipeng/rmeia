package com.ism.rmeia.output;

import com.ism.util.HttpClientUtils;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Created by wx on 2016/10/14.
 */
public class AsyncHttpSendStringUtilsTest {
    static  int LOOP_SIZE=10000;
    List<String> url = new ArrayList<>();
    @Before
    public void setUp() throws InterruptedException {
    }

    static class MyHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            Headers responseHeaders = exchange.getResponseHeaders();
            responseHeaders.set("Content-Type", "text/plain");
            exchange.sendResponseHeaders(200, 0);
            OutputStream responseBody = exchange.getResponseBody();
            Headers requestHeaders = exchange.getRequestHeaders();
            Set<String> keySet = requestHeaders.keySet();
            Iterator<String> iter = keySet.iterator();
            while (iter.hasNext()) {
                String key = iter.next();
                List values = requestHeaders.get(key);
                String s = key + " = " + values.toString() + "\n";
                responseBody.write(s.getBytes());
            }
            responseBody.close();
        }
    }

    private String startSimpleServer(int port) {
        System.out.println("start server:" + port);
        InetSocketAddress addr = new InetSocketAddress(port);
        HttpServer server = null;
        try {
            server = HttpServer.create(addr, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.createContext("/", new MyHandler());
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.println("Server is listening on port " + port);
        return "http://localhost:" + port + "/";
    }


    @Test
    public void asyncSendTest() throws InterruptedException {

        StringBuilder stringBuilder = new StringBuilder(256);
        for (int i = 0; i < 100; i++) {
            stringBuilder.append(i);
        }
        url.clear();
        url.add(startSimpleServer(21111));
        url.add(startSimpleServer(21112));
        url.add(startSimpleServer(21113));

//        url.add("http://192.169.10.19:11111/");
//        url.add("http://192.169.10.19:11112/");
//        url.add("http://192.169.10.19:11113/");
        Thread.sleep(1000);



        int count = 0;
        long diff = 0;
        while (++count < LOOP_SIZE) {

            int i = (int) (Math.random() * LOOP_SIZE) % 3;
            long start = System.currentTimeMillis();

            stringBuilder.setCharAt(stringBuilder.length()-1,(char)(count%256));
            AsyncHttpSendStringUtils.me.asyncSendJson(url.get(i), stringBuilder.toString());
            long end = System.currentTimeMillis();
            if(count%1000 == 0){
                System.out.println(i+":async");
            }
            diff += end-start;
        }
        System.out.println(String.format("asyncSendTest:count=%d,cost=%d", count, diff));
    }

    @Test
    public void sendTest() throws InterruptedException, ExecutionException {
        StringBuilder stringBuilder = new StringBuilder(256);
        for (int i = 0; i < 100; i++) {
            stringBuilder.append(i);
        }
        url.clear();
        url.add(startSimpleServer(31111));
        url.add(startSimpleServer(31112));
        url.add(startSimpleServer(31113));
//        url.add("http://192.169.10.19:11111/");
//        url.add("http://192.169.10.19:11112/");
//        url.add("http://192.169.10.19:11113/");
        Thread.sleep(1000);
        ExecutorService executorService = Executors.newFixedThreadPool(24);


        int count = 0;
        long diff = 0;
        long start = System.currentTimeMillis();

        ArrayList<Future<?>>futures = new ArrayList<>();
        while (++count < LOOP_SIZE) {
            final int i = (int) (Math.random() * LOOP_SIZE) % 3;
            stringBuilder.setCharAt(stringBuilder.length()-1,(char)(count%256));
            Future<?> future = executorService.submit(new Runnable() {
                    int ticker;
                    @Override
                    public void run() {
                        try {
                            HttpClientUtils.getInstance().postAndForget(url.get(i), 5000, stringBuilder.toString(), "application/json", StandardCharsets.UTF_8, false, true, null);
                        } catch (IOException e) {

                        }


                    }
                });
                if(count%1000 == 0){
                   // System.out.println(i);
                   // System.out.println(resp);
                }
            futures.add(future);
        }

        for(Future<?>future:futures){
            future.get();
        }

        long end = System.currentTimeMillis();
        diff += end-start;
        System.out.println(String.format("sendTest:count=%d,cost=%d", count, diff));
    }
}