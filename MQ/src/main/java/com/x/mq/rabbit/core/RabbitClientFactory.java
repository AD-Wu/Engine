package com.x.mq.rabbit.core;

import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.x.mq.common.IMQClient;
import com.x.mq.common.IMQClientFactory;
import com.x.mq.rabbit.data.RabbitConfig;
import com.x.mq.rabbit.data.RabbitMessage;
import com.x.mq.rabbit.data.RabbitParam;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.LongAdder;

/**
 * rabbit客户端工厂,重量级对象
 * 1.不建议创建多个对象
 * 2.当一个连接的Channel太多,需要一个新的Connection(TCP连接)时才创建新的对象
 * @author AD
 * @date 2022/3/23 12:46
 */
public class RabbitClientFactory implements IMQClientFactory<RabbitMessage, RabbitParam> {

    // ------------------------------ 静态变量 ------------------------------

    /**
     * 对象计数器
     */
    private static long factoryCounter;

    // ------------------------------ 成员变量 ------------------------------

    /**
     * rabbit配置
     */
    private final RabbitConfig config;
    /**
     * rabbit连接工厂(创建TCP连接)
     */
    private ConnectionFactory connFactory;

    /**
     * Channel缓存
     */
    private final ThreadLocal<Channel> chnCache;

    /**
     * TCP连接,重量级对象
     */
    private Connection conn;

    /**
     * 消费者线程池
     */
    private ExecutorService executor;

    // ------------------------------ 构造方法 ------------------------------

    /**
     * 构造方法
     * @param config rabbit配置
     */
    public RabbitClientFactory(RabbitConfig config) {
        this.config = config;
        this.connFactory = new ConnectionFactory();
        this.chnCache = new ThreadLocal<>();
    }

    // ------------------------------ 成员方法 ------------------------------

    @Override
    public IMQClient<RabbitMessage, RabbitParam> createClient(RabbitParam param) {
        return new RabbitClient(this, param);
    }

    @Override
    public synchronized void close() throws IOException {
        if (conn != null) {
            try {
                conn.close();
                executor.shutdown();
                conn = null;
                executor = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    // ------------------------------ 受保护方法 ------------------------------

    /**
     * channel用于多线程的话,容易出现问题(官方)
     * @return
     * @throws IOException
     */
    protected Channel getChannel() throws IOException {
        if (initConnection()) {
            Channel chn = chnCache.get();
            if (chn == null || !chn.isOpen()) {
                chnCache.remove();
                chn = conn.createChannel();
                chnCache.set(chn);
            }
            return chn;
        } else {
            throw new IOException("获取Rabbit连接异常|超时");
        }
    }

    protected void closeChannel() {
        Channel chn = chnCache.get();
        if (chn != null) {
            try {
                chnCache.remove();
                chn.close();
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
            }

        }
    }

    // ------------------------------ 私有方法 ------------------------------

    private boolean initConnection() {
        if (conn == null) {
            synchronized (this) {
                if (conn == null) {
                    try {
                        executor = Executors.newFixedThreadPool(config.getThreadCount(), new ThreadFactory() {
                            LongAdder counter = new LongAdder();

                            @Override
                            public Thread newThread(Runnable r) {
                                counter.increment();
                                Thread t = new Thread(r, "Rabbit-" + (++factoryCounter) + "-thread-" + counter.intValue());
                                t.setDaemon(true);
                                return t;
                            }
                        });
                        connFactory.setVirtualHost(config.getVirtualHost());
                        connFactory.setAutomaticRecoveryEnabled(config.isAutoReconnect());
                        connFactory.setUsername(config.getUser());
                        connFactory.setPassword(config.getPwd());
                        connFactory.setSharedExecutor(executor);
                        String[] hosts = config.getHost().split(",");
                        String[] ports = config.getPort().split(",");
                        List<Address> addrs = new ArrayList<>();
                        for (int i = 0; i < hosts.length; i++) {
                            addrs.add(new Address(hosts[i], Integer.parseInt(ports[i])));
                        }
                        conn = connFactory.newConnection(addrs);
                    } catch (IOException | TimeoutException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            }
        }
        return true;
    }


}
