package com.x.doraemon.therad;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.LongAdder;

/**
 * 负载均衡线程池
 *
 * @author AD
 * @date 2021/12/2 23:27
 */
public class LoadBalanceExecutor<T> implements ExecutorService {

    private static volatile LongAdder counter = new LongAdder();
    private String prefixName = "LoadBalance-{}";
    private int startIndex;
    private int offset;
    private final int size;
    private volatile boolean shutdown = true;
    private volatile Map<Integer, ThreadPoolExecutor> pools;
    private volatile Map<T, ThreadPoolExecutor> keyPools;



    public LoadBalanceExecutor(int poolSize) {
        this("", poolSize);
    }

    public LoadBalanceExecutor(String group, int poolSize) {
        this.pools = new ConcurrentHashMap<>();
        this.keyPools = new ConcurrentHashMap<>();
        if (group == null) {
            group = "";
        }
        prefixName = prefixName.replace("{}", group);
        if (!prefixName.endsWith("-")) {
            this.prefixName = prefixName + "-";
        }
        this.size = Math.max(poolSize, 1);
        this.startIndex = counter.intValue();
        this.offset = startIndex;
        counter.add(size);
        for (int i = startIndex; i < startIndex + size; i++) {
            int j = i;
            ThreadPoolExecutor es = (ThreadPoolExecutor) Executors.newFixedThreadPool(1, new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, prefixName + j);
                    return t;
                }
            });
            es.prestartAllCoreThreads();
            pools.put(i, es);
        }
        this.shutdown = false;
    }

    @Override
    public void shutdown() {
        this.shutdown = true;
        Iterator<Entry<Integer, ThreadPoolExecutor>> it = pools.entrySet().iterator();
        while (it.hasNext()) {
            ThreadPoolExecutor pool = it.next().getValue();
            pool.shutdown();
        }
        pools.clear();
        keyPools.clear();
    }

    @Override
    public List<Runnable> shutdownNow() {
        if (isShutdown()) {
            return new ArrayList<>();
        }
        this.shutdown = true;
        Iterator<Entry<Integer, ThreadPoolExecutor>> it = pools.entrySet().iterator();
        List<Runnable> allTasks = new ArrayList<>();
        while (it.hasNext()) {
            ThreadPoolExecutor pool = it.next().getValue();
            List<Runnable> task = pool.shutdownNow();
            allTasks.addAll(task);
        }
        return allTasks;
    }

    @Override
    public boolean isShutdown() {
        return shutdown;
    }

    @Override
    public boolean isTerminated() {
        return shutdown;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        ThreadPoolExecutor executor = getNextExecutor();
        return executor.submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        ThreadPoolExecutor executor = getNextExecutor();
        return executor.submit(task, result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        ThreadPoolExecutor executor = getNextExecutor();
        return executor.submit(task);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
        throws InterruptedException {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public void execute(Runnable command) {
        ThreadPoolExecutor executor = getNextExecutor();
        executor.execute(command);
    }

    public void execute(T key, Runnable command) {
        if (key == null) {
            execute(command);
            return;
        }
        ThreadPoolExecutor executor = keyPools.get(key);
        if (executor == null) {
            executor = getNextExecutor();
            keyPools.put(key, executor);
        }
        executor.execute(command);
    }

    // ----------------------------- 私有方法 -----------------------------
    private ThreadPoolExecutor getNextExecutor() {
        int next = nextIndex();
        return pools.get(next);
    }

    private synchronized int nextIndex() {
        int index = startIndex % size;
        this.startIndex++;
        if (startIndex - offset >= size) {
            startIndex = offset;
        }
        return index + offset;
    }
}
