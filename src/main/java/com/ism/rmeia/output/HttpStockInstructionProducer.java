package com.ism.rmeia.output;

import com.alibaba.fastjson.JSON;
import com.ism.rmeia.bean.UserStockInstruction;
import com.ism.util.HttpClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Collection;

public class HttpStockInstructionProducer extends AbstractProducer<UserStockInstruction>
        implements InstructionProduce<UserStockInstruction>, AggregateProducer<UserStockInstruction> {
    private final static Logger logger = LoggerFactory.getLogger(HttpStockInstructionProducer.class);

    public static HttpStockInstructionProducer me = new HttpStockInstructionProducer();
    String url;
    public HttpStockInstructionProducer() {

    }

    @Override
    public void produce(UserStockInstruction instruction) {
        // 发送格式
        try {
            String json = JSON.toJSONString(instruction);
            // 修改，对不操作的不输出日志数据单独队列发送
            if (!instruction.getOrders().isEmpty()) {
                logger.info("post rmeia instruction {} ", instruction);
                // HttpClientUtils.getInstance().postJSON(url,
                // JSON.toJSONString(instruction), true);
                AsyncHttpSendStringUtils.me.asyncSendJson(url, json);
            } else {
                logger.debug("post rmeia null instruction {}", instruction);
                AsyncHttpSendNoOrders.me.asyncSendJson(url, json);
            }
            
        } catch (Throwable t) {
            logger.error("http stock instruction error", t);
        }
    }

    @Override
    public void produce(Collection<UserStockInstruction> instructions) {
        // 发送一批数据
        try {
            HttpClientUtils.getInstance().postJSON(url, JSON.toJSONString(instructions), true);
            logger.info("post rmeia instruction size={} ", instructions.size());
        } catch (SocketTimeoutException e) {
            logger.error("batch produce error timeout",e);
            e.printStackTrace();
        } catch (IOException e) {
            logger.error("batch produce error",e);
            e.printStackTrace();
        }
    }

    public static HttpStockInstructionProducer getInstance() {
        return me;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public String getUrl(){
        return url;
    }
}
