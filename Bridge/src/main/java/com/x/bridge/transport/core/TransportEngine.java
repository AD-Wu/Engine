package com.x.bridge.transport.core;

import com.x.bridge.bean.Message;
import com.x.bridge.enums.ProxyStatus;
import com.x.bridge.proxy.core.IProxy;
import com.x.doraemon.Strings;
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

    private final IProxy proxy;
    private final IReader reader;
    private final IWriter writer;
    private final ScheduledExecutorService timer;

    public TransportEngine(IProxy proxy, IReader reader, IWriter writer) {
        this.proxy = proxy;
        this.reader = reader;
        this.writer = writer;
        this.timer = Executors.newScheduledThreadPool(1);
    }

    @Override
    public boolean start() {
        timer.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    Message[] msgs = reader.read();
                    getReceiver().receive(msgs);
                    proxy.status(ProxyStatus.running);
                } catch (Exception e) {
                    proxy.status(ProxyStatus.transportError);
                    e.printStackTrace();
                    log.error("传输引擎执行读取异常:{}", Strings.getExceptionTrace(e));
                }
            }
        }, 0, 0, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void stop() {
        timer.shutdown();
        proxy.status(ProxyStatus.transportError);
    }


    @Override
    public void write(Message... messages) {
        try {
            this.writer.write(messages);
            proxy.status(ProxyStatus.running);
        } catch (Exception e) {
            proxy.status(ProxyStatus.transportError);
            e.printStackTrace();
            log.error("写入数据失败:{}", Strings.getExceptionTrace(e));
        }
    }

    @Override
    public IReceiver getReceiver() {
        return proxy.getSessionManager();
    }
}
