package com.x.doraemon.therad;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.LongAdder;

/**
 * 负载均衡线程池
 * @author AD
 * @date 2021/12/2 23:27
 */
public class BalanceExecutor<T> implements IBalanceExecutor<T> {

    private static final Map<String, LongAdder> counters = new HashMap<>();

    private LongAdder counter;
    private final String prefix;
    private int next;
    private final int end;
    private final int size;
    private volatile boolean shutdown = true;
    private volatile boolean awaitTermination = false;
    private Map<Integer, ThreadPoolExecutor> pools;
    private Map<T, ThreadPoolExecutor> keyPools;

    public BalanceExecutor() {
        this("", Runtime.getRuntime().availableProcessors() * 2);
    }

    public BalanceExecutor(String group) {
        this(group, Runtime.getRuntime().availableProcessors() * 2);
    }

    public BalanceExecutor(int poolSize) {
        this("", poolSize);
    }

    public BalanceExecutor(String group, int poolSize) {
        this.pools = new ConcurrentHashMap<>();
        this.keyPools = new ConcurrentHashMap<>();
        this.counter = getCounter(group);
        this.prefix = fixPrefix("Balance-{}", group);
        this.size = Math.max(poolSize, 1);
        this.next = counter.intValue();
        this.end = next + size - 1;
        counter.add(size);
        for (int i = next; i < next + size; i++) {
            String name = prefix + i;
            ThreadPoolExecutor es = (ThreadPoolExecutor) Executors.newFixedThreadPool(1, new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, name);
                }
            });
            es.prestartAllCoreThreads();
            pools.put(i, es);
        }
        this.shutdown = false;
    }


    @Override
    public void execute(T key, Runnable command) {
        if (key == null) {
            execute(command);
            return;
        }
        ThreadPoolExecutor executor = keyPools.get(key);
        if (executor == null) {
            synchronized (this) {
                if (executor == null) {
                    executor = getNextExecutor();
                    keyPools.put(key, executor);
                }
            }
        }
        executor.execute(command);
    }

    @Override
    public void execute(Runnable command) {
        ThreadPoolExecutor executor = getNextExecutor();
        executor.execute(command);
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
    public synchronized void shutdown() {
        this.awaitTermination = true;
        Iterator<Entry<Integer, ThreadPoolExecutor>> it = pools.entrySet().iterator();
        while (it.hasNext()) {
            ThreadPoolExecutor pool = it.next().getValue();
            pool.shutdown();
        }
        pools.clear();
        keyPools.clear();
        this.shutdown = true;
        this.awaitTermination = false;
    }

    @Override
    public synchronized List<Runnable> shutdownNow() {
        if (shutdown) {
            return new ArrayList<>();
        }
        this.awaitTermination = true;
        Iterator<Entry<Integer, ThreadPoolExecutor>> it = pools.entrySet().iterator();
        List<Runnable> allTasks = new ArrayList<>();
        while (it.hasNext()) {
            ThreadPoolExecutor pool = it.next().getValue();
            List<Runnable> task = pool.shutdownNow();
            allTasks.addAll(task);
        }
        this.shutdown = true;
        this.awaitTermination = false;
        return allTasks;
    }

    @Override
    public synchronized boolean isShutdown() {
        return shutdown;
    }

    @Override
    public synchronized boolean isTerminated() {
        return shutdown;
    }

    @Override
    public synchronized boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return awaitTermination;
    }


    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        List<Future<T>> futures = new ArrayList<>(tasks.size());
        for (Callable<T> task : tasks) {
            ThreadPoolExecutor ex = getNextExecutor();
            Future<T> f = ex.submit(task);
            futures.add(f);
        }
        return futures;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
        throws InterruptedException {
        List<Future<T>> futures = new ArrayList<>(tasks.size());
        for (Callable<T> task : tasks) {
            List<Callable<T>> ts = new ArrayList<>(1);
            ts.add(task);
            ThreadPoolExecutor ex = getNextExecutor();
            List<Future<T>> fs = ex.invokeAll(ts, timeout, unit);
            if (!fs.isEmpty()) {
                futures.add(fs.get(0));
            }
        }
        return futures;
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        ThreadPoolExecutor ex = getNextExecutor();
        T t = ex.invokeAny(tasks);
        return t;
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException {
        ThreadPoolExecutor ex = getNextExecutor();
        T t = ex.invokeAny(tasks, timeout, unit);
        return t;
    }

    // ----------------------------- 私有方法 -----------------------------
    private LongAdder getCounter(String group) {
        if (!counters.containsKey(group)) {
            counters.put(group, new LongAdder());
        }
        return counters.get(group);
    }

    private String fixPrefix(String prefix, String group) {
        if (group == null) {
            group = "";
        }
        prefix = prefix.replace("{}", group);
        if (!prefix.endsWith("-")) {
            prefix = prefix + "-";
        }
        return prefix;
    }

    private ThreadPoolExecutor getNextExecutor() {
        int next = nextIndex();
        return pools.get(next);
    }

    private synchronized int nextIndex() {
        if (next > end) {
            next = end - size + 1;
        }
        return next++;
    }

    public static void main(String[] args) {
        IBalanceExecutor<String> main = new BalanceExecutor<>();
        main.execute(new Runnable() {
            @Override
            public void run() {
                IBalanceExecutor<String> executor = new BalanceExecutor<>(2);
                while (true) {
                    executor.execute("a", new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(Thread.currentThread().getName() + "-a");
                        }
                    });
                    executor.execute("b", new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(Thread.currentThread().getName() + "-b");
                        }
                    });
                    executor.execute("c", new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(Thread.currentThread().getName() + "-c");
                        }
                    });
                    executor.execute("d", new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(Thread.currentThread().getName() + "-d");
                        }
                    });
                    try {
                        TimeUnit.SECONDS.sleep(3);
                        System.out.println("-----------------");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }
}
