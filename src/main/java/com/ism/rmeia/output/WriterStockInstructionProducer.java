package com.ism.rmeia.output;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

import org.apache.http.annotation.NotThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.ism.rmeia.bean.UserStockInstruction;

/**
 * 这个适用于测试输出
 *
 * @version 1.0
 * @since 1.0
 */
@NotThreadSafe
public class WriterStockInstructionProducer extends AbstractProducer<UserStockInstruction> {
    private final static Logger logger = LoggerFactory.getLogger(WriterStockInstructionProducer.class);
    String json;
    Writer wr;

    public WriterStockInstructionProducer() {

    }

    @Override
    public void produce(UserStockInstruction instruction) {
        // 发送格式
        String json = JSON.toJSONString(instruction);
        try {
            wr.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void produce(Collection<UserStockInstruction> instructions) {
        // 发送一批数据
        String json = JSON.toJSONString(instructions);
        try {
            wr.write(json);
        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 设置向哪个通道写入
     * 
     * @param wr
     */
    public void setWriter(Writer wr) {
        this.wr = wr;
    }
}
