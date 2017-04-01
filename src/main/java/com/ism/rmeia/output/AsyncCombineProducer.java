package com.ism.rmeia.output;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Time;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * Created by wx on 2016/10/13.
 * @since 1.0.1
 * @version 1.0.1
 */
public class AsyncCombineProducer<U> extends AbstractProducer<U>{
   private final static Logger logger = LoggerFactory.getLogger(AsyncCombineProducer.class);

   ScheduledExecutorService singleThread = Executors.newSingleThreadScheduledExecutor();
    //Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    Consumer<U> errorHandler; // if add queue failed, do errorHandler
    TimeoutAndCacheQueueTask<U> queueTask;
    String url;


    public AsyncCombineProducer(Consumer<Collection<U>> consumer,int blockSize,long timeout){
        this(consumer,new ArrayBlockingQueue<U>(blockSize),blockSize,timeout,Executors.newCachedThreadPool());
    }
    public AsyncCombineProducer(Consumer<Collection<U>> consumer, BlockingQueue<U> blockingQueue,int blockSize,long timeout,ExecutorService es){
        queueTask  = new TimeoutAndCacheQueueTask<>(consumer,blockingQueue,es);
        queueTask.setUnderflow(Math.max(16,blockSize*3/4));
        queueTask.setTimeout(timeout);
        queueTask.setLastRuntime(System.currentTimeMillis());
        singleThread.scheduleWithFixedDelay(queueTask,0,queueTask.getTimeout(),TimeUnit.MILLISECONDS);
    }
    @Override
    public void produce(Collection<U> c) {
        if(c!=null){
           for(U e : c){
               produce(e);
           }
        }
    }

    @Override
    public void produce(U t) {
        if(t!=null){
            try {
                boolean addSuccess = getBlockingQueue().offer(t,5000, TimeUnit.MILLISECONDS);
                if(!addSuccess){
                    //do error handler;
                    if(errorHandler !=null) {
                        errorHandler.accept(t);
                    }
                    logger.warn("[AsyncCombineProducer:produce] add item failed, system is blocking.{}",t);
                }
                if(queueTask.isQueueFull()){
                    queueTask.sendIfTimeoutOrQueueFull(System.currentTimeMillis());
                }
            } catch (InterruptedException e) {
            }
        }
    }




    public BlockingQueue<U> getBlockingQueue() {
        return queueTask.blockingQueue;
    }

    public void setBlockingQueue(BlockingQueue<U> blockingQueue) {
        queueTask.setBlockingQueue(blockingQueue);
    }

    public void setUnderflow(int underflow) {
        queueTask.setUnderflow(underflow);
    }

    public void setExecutorService(ExecutorService executorService) {
        queueTask.setExecutorService(executorService);
    }

    public long getLastRuntime() {
        return queueTask.getLastRuntime();
    }

    public void setTimeout(long timeout) {
        queueTask.setTimeout(timeout);
    }

    public void setLastRuntime(long lastRuntime) {
        queueTask.setLastRuntime(lastRuntime);
    }

    public int getUnderflow() {
        return queueTask.getUnderflow();
    }

    public long getTimeout() {
        return queueTask.getTimeout();
    }

    public ExecutorService getExecutorService() {
        return queueTask.getExecutorService();
    }

    public Consumer<Collection<U>> getConsumer() {
        return queueTask.getConsumer();
    }

    public void setConsumer(Consumer<Collection<U>> consumer) {
        queueTask.setConsumer(consumer);
    }

    /**
     * error handler,used when if consumer this message eror, do handler error with the input.
     *
     * @return error handler
     */
    public Consumer<U> getErrorHandler() {
        return errorHandler;
    }

    public void setErrorHandler(Consumer<U> errorHandler) {
        this.errorHandler = errorHandler;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "AsyncCombineProducer{" +
            "errorHandler=" + errorHandler +
            ", queueTask=" + queueTask +
            '}';
    }
}
