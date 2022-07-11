package com.x.bridge.proxy.factory;

import com.x.bridge.bean.Message;
import com.x.bridge.enums.ProxyStatus;
import com.x.bridge.proxy.interfaces.IProxy;
import com.x.bridge.transport.interfaces.ITransportEngine;

/**
 * @author AD
 * @date 2022/7/11 19:49
 */
public class Proxy implements IProxy {
    
    /**
     * 传输引擎
     */
    private final ITransportEngine<Message> te;
    
   
    
    public Proxy(ITransportEngine<Message> te){
        this.te = te;
    }

    @Override
    public ProxyStatus status() {
        return null;
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public boolean isServerMode() {
        return false;
    }

    @Override
    public synchronized boolean start() {
        if(te.start()){
        
        }else{
        
        }
        return false;
    }

    @Override
    public synchronized void stop() {

    }

    @Override
    public void receive(Message... messages) {

    }

    @Override
    public void send(Message... messages) {

    }
}
