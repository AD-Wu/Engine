package com.x.bridge.transport.core;

import com.x.bridge.bean.Message;

import com.x.bridge.enums.TransporterStatus;

import com.x.bridge.interfaces.Service;
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
public class Transporter extends Service implements ITransporter {

    private static final int maxBytes = 70000;
    private final IProxy proxy;
    private final IReader reader;
    private final IWriter writer;
    protected volatile TransporterStatus status;

    private final ScheduledExecutorService readerExecutor = Executors.newScheduledThreadPool(1);
    private final ExecutorService writerExecutor = new BalanceExecutor<String>("Writer", 1);
    private final ArrayBlockingQueue<Message> queue = new ArrayBlockingQueue<>(Integer.MAX_VALUE);

    public Transporter(IProxy proxy, IReader reader, IWriter writer) {
        this.proxy = proxy;
        this.reader = reader;
        this.writer = writer;
        this.status = TransporterStatus.stopped;
    }

    @Override
    protected boolean onStart() throws Exception {
        status = TransporterStatus.running;
        readerExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    Message[] msgs = reader.read();
                    status = TransporterStatus.running;
                    IReceiver receiver = getReceiver();
                    if (receiver != null) {
                        receiver.receive(msgs);
                    }
                } catch (Exception e) {
                    status = TransporterStatus.error;
                    e.printStackTrace();
                    log.error("传输引擎执行读取异常:{}", Strings.getExceptionTrace(e));
                }
            }
        }, 0, 0, TimeUnit.MILLISECONDS);

        writerExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (status == TransporterStatus.running) {
                        try {
                            Message[] msgs = getMessages();
                            Transporter.this.writer.write(msgs);
                        } catch (Exception e) {
                            status = TransporterStatus.error;
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
        status = TransporterStatus.stopped;
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
        if (status == TransporterStatus.running) {
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
    public IReceiver getReceiver() {
        return proxy.getSessionManager();
    }

    @Override
    public TransporterStatus status() {
        return status;
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
