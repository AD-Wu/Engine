package com.x.bridge.transport.core;

import com.x.bridge.bean.Message;

import com.x.bridge.enums.TransportEngineStatus;

import com.x.bridge.proxy.core.IProxy;
import com.x.doraemon.Strings;
import com.x.doraemon.therad.BalanceExecutor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.log4j.Log4j2;

/**
 * @author AD
 * @date 2022/7/11 19:52
 */
@Log4j2
public class TransportEngine implements ITransportEngine {

    private static final int maxBytes = 70000;
    private final IProxy proxy;
    private final IReader reader;
    private final IWriter writer;
    protected volatile TransportEngineStatus status;

    private final ScheduledExecutorService readerExecutor = Executors.newScheduledThreadPool(1);
    private final ExecutorService writerExecutor = new BalanceExecutor<String>("Writer", 1);
    private final ArrayBlockingQueue<Message> writeQueue = new ArrayBlockingQueue<>(Integer.MAX_VALUE);

    public TransportEngine(IProxy proxy, IReader reader, IWriter writer) {
        this.proxy = proxy;
        this.reader = reader;
        this.writer = writer;
        this.status = TransportEngineStatus.stopped;
    }

    @Override
    public boolean start() {
        readerExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    Message[] msgs = reader.read();
                    status = TransportEngineStatus.running;
                    IReceiver receiver = getReceiver();
                    if (receiver != null) {
                        receiver.receive(msgs);
                    }
                } catch (Exception e) {
                    status = TransportEngineStatus.error;
                    e.printStackTrace();
                    log.error("传输引擎执行读取异常:{}", Strings.getExceptionTrace(e));
                }
            }
        }, 0, 0, TimeUnit.MILLISECONDS);

        writerExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (status == TransportEngineStatus.running) {
                        try {
                            Message[] msgs = getMessages();
                            TransportEngine.this.writer.write(msgs);
                        } catch (Exception e) {
                            status = TransportEngineStatus.error;
                            e.printStackTrace();
                            log.error("写入数据失败:{}", Strings.getExceptionTrace(e));
                        }
                    } else {
                        log.error("无法写入,传输引擎状态【{}】", status);
                    }
                }
            }
        });
        return true;
    }

    @Override
    public void stop() {
        readerExecutor.shutdown();
        writerExecutor.shutdown();
        status = TransportEngineStatus.stopped;
    }


    @Override
    public void write(Message... msgs) throws Exception {
        if (status == TransportEngineStatus.running) {
            if (msgs != null || msgs.length > 0) {
                for (int i = 0, c = msgs.length; i < c; i++) {
                    writeQueue.add(msgs[i]);
                }
            }
        } else {
            throw new RuntimeException("无法将数据存入发送队列,传输引擎状态【" + status + "】");
        }

    }

    @Override
    public IReceiver getReceiver() {
        return proxy.getSessionManager();
    }

    @Override
    public TransportEngineStatus status() {
        return status;
    }

    private Message[] getMessages() {
        List<Message> msgs = new ArrayList<>();
        int sumBytes = 0;
        while (!writeQueue.isEmpty() && (sumBytes = getSumBytes(sumBytes)) <= maxBytes) {
            msgs.add(writeQueue.poll());
        }
        if (sumBytes > 0) {
            log.info("数据域字节总数【{}】", sumBytes);
        }
        return msgs.toArray(new Message[0]);
    }

    private int getSumBytes(int sumBytes) {
        byte[] data = writeQueue.peek().getData();
        return data == null ? sumBytes : sumBytes + data.length;
    }
}
