package com.ism.rmeia.output;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ism.util.Config;

public class AsyncHttpSendNoOrders {
    private final static Logger logger = LoggerFactory.getLogger(AsyncHttpSendNoOrders.class);
    public static AsyncHttpSendNoOrders me = new AsyncHttpSendNoOrders();
    Map<String,AsyncCombineProducer<String>> asyncHttpCombineProducerMap = new ConcurrentHashMap<>(16);
    Config config = Config.getInstance();
    int NullQueueSize = 3072;
    int Nulltimeout = 10;
    private AsyncHttpSendNoOrders() {

    }

    public AsyncHttpSendNoOrders getInstance() {
        return me;
    }

    public void asyncSendJson(String url, String content) throws RuntimeException {
        try {
            AsyncCombineProducer<String> producer = null;
            if (asyncHttpCombineProducerMap.get(url) == null) {
                CombineHttpSendJson<String> httpConsumer = new CombineHttpSendJson<>();
                httpConsumer.setUrl(url);
                NullQueueSize = config.getInt("null.queue.size",3072);
                Nulltimeout = config.getInt("null.send.timeout",10);
                producer = new AsyncCombineProducer<>(httpConsumer,NullQueueSize,Nulltimeout*1000);
                asyncHttpCombineProducerMap.putIfAbsent(url,producer);
            }
            producer = asyncHttpCombineProducerMap.get(url);
            producer.produce(content);//处理content消息+
        } catch (Throwable t) {
            logger.error("aync null send error", t.getMessage());
            throw new RuntimeException(t);
        }
    }
    public void configure(Config config) {
        this.config = config;
    }
}
