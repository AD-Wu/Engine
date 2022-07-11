package com.x.bridge.transport.factory;

import com.x.bridge.bean.Message;
import com.x.bridge.enums.TransportEngineStatus;
import com.x.bridge.proxy.interfaces.IReceiver;
import com.x.bridge.transport.interfaces.ITransportEngine;

/**
 * @author AD
 * @date 2022/7/11 19:52
 */
public class DBTransportEngine implements ITransportEngine<Message> {
    
    private final IReceiver<Message> receiver;
    
    private volatile TransportEngineStatus status;
    
    public DBTransportEngine(IReceiver<Message> receiver) {
        this.receiver = receiver;
        this.status = TransportEngineStatus.stopped;
    }
    
    @Override
    public boolean start() {
        return false;
    }
    
    @Override
    public void stop() {
    
    }
    
    @Override
    public TransportEngineStatus status() {
        return status;
    }
    
    @Override
    public void send(Message... messages) {
    
    }
    
}
