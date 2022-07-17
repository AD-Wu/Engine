package com.x.bridge.transport.core;

import com.x.bridge.proxy.core.IProxyService;
import com.x.bridge.proxy.core.Service;
import com.x.bridge.bean.SessionMsg;
import com.x.bridge.proxy.enums.TransporterStatus;
import com.x.doraemon.Strings;
import com.x.doraemon.therad.BalanceExecutor;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author AD
 * @date 2022/7/11 19:52
 */
@Log4j2
public class Transporter extends Service implements ITransporter {

    private static final int maxBytes = 70000;
    private final IProxyService proxy;
    private final IReader<SessionMsg> reader;
    private final IWriter<SessionMsg> writer;
    protected volatile TransporterStatus status;

    private final ScheduledExecutorService readerExecutor = Executors.newScheduledThreadPool(1);
    private final ExecutorService writerExecutor = new BalanceExecutor<String>("Writer", 1);
    private final ArrayBlockingQueue<SessionMsg> queue = new ArrayBlockingQueue<>(Integer.MAX_VALUE);

    public Transporter(IProxyService proxy, IReader<SessionMsg> reader, IWriter<SessionMsg> writer) {
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
                    SessionMsg[] msgs = reader.read();
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
                            SessionMsg[] msgs = getMessages();
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
    public void write(SessionMsg... msgs) throws Exception {
        if (status == TransporterStatus.running) {
            if (msgs != null || msgs.length > 0) {
                for (int i = 0, c = msgs.length; i < c; i++) {
                    SessionMsg msg = msgs[i];
                    queue.add(msg);
                }
            }
        } else {
            throw new RuntimeException("无法将数据存入发送队列,传输引擎状态【" + status + "】");
        }

    }

    @Override
    public IReceiver getReceiver() {
        return proxy;
    }

    @Override
    public TransporterStatus status() {
        return status;
    }

    @Override
    public void clear() {
        writer.clear();
    }

    private SessionMsg[] getMessages() {
        List<SessionMsg> msgs = new ArrayList<>();
        int sumBytes = 0;
        while (!queue.isEmpty() && (sumBytes = getSumBytes(sumBytes)) <= maxBytes) {
            msgs.add(queue.poll());
        }
        if (sumBytes > 0) {
            log.info("数据域字节总数【{}】", sumBytes);
        }
        return msgs.toArray(new SessionMsg[0]);
    }

    private int getSumBytes(int sumBytes) {
        byte[] data = queue.peek().getData();
        return data == null ? sumBytes : sumBytes + data.length;
    }
}
