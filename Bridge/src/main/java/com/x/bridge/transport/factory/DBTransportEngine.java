package com.x.bridge.transport.factory;

import com.x.bridge.bean.Message;
import com.x.bridge.enums.TransportEngineStatus;
import com.x.bridge.transport.interfaces.IReceiver;
import com.x.bridge.transport.interfaces.IReader;
import com.x.bridge.transport.interfaces.ITransportEngine;
import com.x.bridge.transport.interfaces.IWriter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author AD
 * @date 2022/7/11 19:52
 */
public class DBTransportEngine implements ITransportEngine {

    private final IReceiver<Message> receiver;

    private final boolean serverMode;

    private final IWriter<Message> writer;
    private final IReader<Message> reader;

    private final ScheduledExecutorService timer;

    private volatile TransportEngineStatus status;

    public DBTransportEngine(IReceiver<Message> receiver, IWriter<Message> writer, IReader<Message> reader, boolean serverMode) {
        this.receiver = receiver;
        this.writer = writer;
        this.reader = reader;
        this.timer = Executors.newScheduledThreadPool(1);
        this.serverMode = serverMode;
        this.status = TransportEngineStatus.stopped;
    }

    @Override
    public boolean start() {
        timer.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    Message[] msgs = reader.read();
                    DBTransportEngine.this.status = TransportEngineStatus.running;
                    receiver.receive(msgs);
                } catch (Exception e) {
                    DBTransportEngine.this.status = TransportEngineStatus.error;
                    e.printStackTrace();
                }
            }
        }, 0, 0, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void stop() {

    }

    @Override
    public TransportEngineStatus status() {
        return status;
    }

    @Override
    public void write(Message... messages) {
        try {
            this.writer.write(messages);
        } catch (Exception e) {
            DBTransportEngine.this.status = TransportEngineStatus.error;
            e.printStackTrace();
        }
    }

}
