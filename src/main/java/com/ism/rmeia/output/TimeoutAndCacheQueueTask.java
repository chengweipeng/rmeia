package com.ism.rmeia.output;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * Created by wx on 2016/10/18.
 * 测试大量数据发送时的结果显示：不启用future.get，约快1/5，注future.get是一个同步方法
 * 合并多步处理到一个处理中，特别适合合并异步HTTP请求
 * @since 1.0.1
 * @version 1.0.1
 */
public  class TimeoutAndCacheQueueTask<T> implements Runnable,Callable<Future<Integer>>{
    private final static Logger logger = LoggerFactory.getLogger(TimeoutAndCacheQueueTask.class);
    BlockingQueue<T> blockingQueue ;
    long lastRuntime = 0;//TimeUnit.MillSeconds
    long timeout = 20*1000;
    int underflow = 1;
    ExecutorService executorService;
    Consumer<Collection<T>> consumer;

    boolean syncFutureGet;//是否启用future


    public TimeoutAndCacheQueueTask(Consumer<Collection<T>> consumer, BlockingQueue<T> blockingQueue,ExecutorService es){
        this.consumer = consumer;
        this.blockingQueue = blockingQueue;
        executorService = es;
        syncFutureGet = true;
    }

    @Override
    public Future<Integer> call() throws Exception {
        Objects.nonNull(blockingQueue);
        try {
            Future<Integer> future = sendIfTimeoutOrQueueFull(System.currentTimeMillis());
            return  future;
        }catch (Throwable t){
            t.printStackTrace();
            throw new RuntimeException("sendIfTimeoutOrQueueFull error",t);
        }
    }
    /**
     * swallow all exception
     */
    @Override
    public void run() {
        Objects.nonNull(blockingQueue);
        try {
            Future<Integer> future = call();
            if(syncFutureGet && future != null){
                future.get();
            }
        }catch (Throwable t){
            t.printStackTrace();
            logger.error("batch process exception",t);
        }
    }
    public boolean isQueueTimeOut(long now){
        return now - lastRuntime > timeout;
    }
    public boolean isQueueFull() {
        return isQueueFull(blockingQueue.size(), underflow);
    }
    private static<T> boolean isQueueFull(int size,int underflow) {
        return size > underflow;
    }

    /**
     *需要小心锁与同步处理
     * @return future
     */
    public Future<Integer> fireSendEvent(int size){
        if(executorService == null){
            executorService = Executors.newCachedThreadPool();
        }
        if(executorService != null){
            Future<Integer> future = executorService.submit(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    ArrayList<T> localDumpList = new ArrayList<>(size); //only used for loop variable
                    blockingQueue.drainTo(localDumpList);
                    final Collection<T> datum  = localDumpList;
                    consumer.accept(datum);
                    return datum.size();
                }
            });
            return future;
        }
        return null;
    }
    public Future<Integer> sendIfTimeoutOrQueueFull(long curTime){

        BlockingQueue<T> queue = blockingQueue;
        boolean isTimeout = isQueueTimeOut(curTime);
        int size = queue.size();
        boolean queueFull =  isQueueFull(size,underflow);
        if(isTimeout || queueFull){
            logger.debug("[QueueTask.parm],timeout={},queueFull={}", timeout, underflow);
            if(!queue.isEmpty()) {
                logger.debug("[QueueTask.result],timeout={},queueFull={}", isTimeout, queueFull);
                Future<Integer> future = fireSendEvent(size+32);
                setLastRuntime(curTime);
                return future;
            }
        }
        return null;
    }

    public BlockingQueue<T> getBlockingQueue() {
        return blockingQueue;
    }

    public void setBlockingQueue(BlockingQueue<T> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    public long getLastRuntime() {
        return lastRuntime;
    }

    public void setLastRuntime(long lastRuntime) {
        this.lastRuntime = lastRuntime;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public int getUnderflow() {
        return underflow;
    }

    public void setUnderflow(int underflow) {
        this.underflow = underflow;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public Consumer<Collection<T>> getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer<Collection<T>> consumer) {
        this.consumer = consumer;
    }

    @Override
    public String toString() {
        return "TimeoutAndCacheQueueTask{" +
            "lastRuntime=" + lastRuntime +
            ", timeout=" + timeout +
            ", underflow=" + underflow +
            ", blockingQueue=" + blockingQueue +
            ", consumer=" + consumer +
            ", executorService=" + executorService +
            '}';
    }

    public boolean isSyncFutureGet() {
        return syncFutureGet;
    }

    public void setSyncFutureGet(boolean syncFutureGet) {
        this.syncFutureGet = syncFutureGet;
    }


}