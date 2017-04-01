package com.ism.market.consumer;

public interface RecordConsumer<V> {
    public void consume(V content)throws Exception;
}
