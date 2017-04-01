package com.ism.market.hbase;



import com.ism.market.consumer.RecordConsumer;
import org.apache.hadoop.hbase.client.Result;

public interface HbaseGetConsumer extends RecordConsumer<Result> {
    @Override
    public void consume(Result result) throws Exception;
}
