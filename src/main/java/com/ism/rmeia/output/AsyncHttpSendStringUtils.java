package com.ism.rmeia.output;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ism.rmeia.cache.loader.SimpleCacheLoader;
import com.ism.util.Config;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Created by wx on 2016/10/14.
 * 为每个url生成一个异步处理队列，每个异步队列都有不固定的线程数的线程池
 * 异步队列默认是固定大小的循环队列
 * @since 1.0.1
 * @version 1.0.1
 *
 */
public class AsyncHttpSendStringUtils {
    private final static Logger logger = LoggerFactory.getLogger(AsyncHttpSendStringUtils.class);
    public static AsyncHttpSendStringUtils me = new AsyncHttpSendStringUtils();

    Map<String,AsyncCombineProducer<String>> asyncHttpCombineProducerMap = new ConcurrentHashMap<>(16);
    int blockQueueSize = 4096;
    int timeout = 20;
    Config config = Config.getInstance();
    private AsyncHttpSendStringUtils(){
    }
    public static AsyncHttpSendStringUtils getInstance() {
        return me;
    }
    public void asyncSendJson(String url, String content) throws RuntimeException {
        try {
            AsyncCombineProducer<String> producer = null;
            if (asyncHttpCombineProducerMap.get(url) == null) {
                CombineHttpSendJson<String> httpConsumer = new CombineHttpSendJson<>();
                httpConsumer.setUrl(url);
                blockQueueSize = config.getInt("queue.size",4096);
                timeout = config.getInt("send.timeout",20);
                producer = new AsyncCombineProducer<>(httpConsumer,blockQueueSize,timeout*1000);
                asyncHttpCombineProducerMap.putIfAbsent(url,producer);
            }
            producer = asyncHttpCombineProducerMap.get(url);
            producer.produce(content);//处理content消息+
        }catch (Throwable t){
            logger.error("aync send error",t.getMessage());
            throw new RuntimeException(t);
        }
    }

    @Override
    public String toString() {
        return "AsyncHttpSendStringUtils{" +
            "asyncHttpCombineProducerMap=" + asyncHttpCombineProducerMap +
            ", blockQueueSize=" + blockQueueSize +
            '}';
    }
    
    public void configure(Config config) {
        this.config = config;
    }
}
