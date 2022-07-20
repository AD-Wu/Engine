package com.x.bridge.bus.core;

import com.x.bridge.bean.Message;
import com.x.bridge.netty.common.Service;
import com.x.bridge.proxy.core.IProxyService;
import com.x.bridge.proxy.enums.BusStatus;
import com.x.doraemon.Strings;
import com.x.doraemon.therad.BalanceExecutor;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * @author AD
 * @date 2022/7/11 19:52
 */
@Log4j2
public class Bus extends Service implements IBus<Message>{

    private static final int maxBytes = 70000;
    private final IProxyService proxy;
    private final IReader<Message> reader;
    private final IWriter<Message> writer;
    protected volatile BusStatus status;

    private final ScheduledExecutorService readerExecutor = Executors.newScheduledThreadPool(1);
    private final ExecutorService writerExecutor = new BalanceExecutor<String>("Writer", 1);
    private final Queue<Message> queue = new ArrayBlockingQueue<>(Integer.MAX_VALUE);

    public Bus(IProxyService proxy, IReader<Message> reader, IWriter<Message> writer) {
        this.proxy = proxy;
        this.reader = reader;
        this.writer = writer;
        this.status = BusStatus.stopped;
    }

    @Override
    protected boolean onStart() throws Exception {
        status = BusStatus.running;
        readerExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    Message[] msgs = reader.read();
                    status = BusStatus.running;
                    IReceiver receiver = receiver();
                    if (receiver != null) {
                        receiver.receive(msgs);
                    }
                } catch (Exception e) {
                    status = BusStatus.error;
                    e.printStackTrace();
                    log.error("传输引擎执行读取异常:{}", Strings.getExceptionTrace(e));
                }
            }
        }, 0, 0, TimeUnit.MILLISECONDS);

        writerExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (status == BusStatus.running) {
                        try {
                            Message[] msgs = getMessages();
                            Bus.this.writer.write(msgs);
                        } catch (Exception e) {
                            status = BusStatus.error;
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
    protected void onStop() {
        status = BusStatus.stopped;
        readerExecutor.shutdown();
        writerExecutor.shutdown();
        queue.clear();
    }

    @Override
    protected void onStartError(Throwable e) {
        stop();
    }


    @Override
    public void write(Message... msgs) throws Exception {
        if (status == BusStatus.running) {
            if (msgs != null || msgs.length > 0) {
                for (int i = 0, c = msgs.length; i < c; i++) {
                    Message msg = msgs[i];
                    queue.add(msg);
                }
            }
        } else {
            throw new RuntimeException("无法将数据存入发送队列,传输引擎状态【" + status + "】");
        }

    }

    @Override
    public IReceiver receiver() {
        return proxy;
    }

    @Override
    public BusStatus status() {
        return status;
    }
    
    
    @Override
    public void clear() {
        writer.clear();
    }

    private Message[] getMessages() {
        List<Message> msgs = new ArrayList<>();
        int sumBytes = 0;
        while (!queue.isEmpty() && (sumBytes = getSumBytes(sumBytes)) <= maxBytes) {
            msgs.add(queue.poll());
        }
        if (sumBytes > 0) {
            log.info("数据域字节总数【{}】", sumBytes);
        }
        return msgs.toArray(new Message[0]);
    }

    private int getSumBytes(int sumBytes) {
        byte[] data = queue.peek().getData();
        return data == null ? sumBytes : sumBytes + data.length;
    }
}
