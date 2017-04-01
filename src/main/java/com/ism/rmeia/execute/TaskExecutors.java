package com.ism.rmeia.execute;

import com.google.common.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class TaskExecutors {

    private final static Logger logger = LoggerFactory.getLogger(TaskExecutors.class);
    ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2, new ThreadFactory() {
        volatile long cur = 0;

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName("pool-" + cur++);
            t.setDaemon(true);
            return t;
        }
    });
    ListeningExecutorService guavaSchedule = MoreExecutors.listeningDecorator(executor);

    private static TaskExecutors taskExecutor = new TaskExecutors();

    // 除了ListenableFuture,guava还提供了FutureCallback接口,相对来说更加方便一些.

    volatile int threadId;

    protected TaskExecutors() {
    }


    public static TaskExecutors getInstance() {
        return taskExecutor;
    }

    public ExecutorService getExecutors() {
        return executor;
    }

    public <V> Future<V> addTask(Callable<V> task) {
        return executor.submit(task);
    }

    /**
     * 同步阻塞的方式
     *
     * @param task 提交任务
     */
    public <V> V addSyncTask(Callable<V> task) {
        logger.debug("sync task begin ...");
        Future<V> future = executor.submit(task);
        try {
            while (true) {
                try {
                    return future.get();
                } catch (InterruptedException e) {

                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        logger.debug("sync task end ... ");
        return null;
    }

    /**
     * 提交任务,带回调
     *
     * @param task     提交任务
     * @param callback 回调
     */
    public <V> void addTask(Callable<V> task, FutureCallback<V> callback) {
        ListenableFuture<? extends V> guavaListenableFuture = guavaSchedule.submit(task);
        Futures.addCallback(guavaListenableFuture, callback);
    }

    /**
     * 提交任务,带回调
     *
     * @param task     提交任务
     * @param callback 回调
     * @param executor
     */
    public <V> void addTask(Callable<V> task, FutureCallback<V> callback, Executor executor) {
        ListenableFuture<? extends V> guavaListenableFuture = guavaSchedule.submit(task);
        Futures.addCallback(guavaListenableFuture, callback, executor);
    }

    public <V> void addTask(Callable<? extends V> task, long delay, TimeUnit unit, FutureCallback<V> callback) {
        ListenableFuture<? extends V> guavaListenableFuture = guavaSchedule.submit(task);
        Futures.addCallback(guavaListenableFuture, callback);
    }

}
