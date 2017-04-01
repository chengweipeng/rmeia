package com.ism.rmeia.output;

import java.util.Collection;

public interface AggregateProducer<T> {
    public void produce(Collection<T> c);
}
