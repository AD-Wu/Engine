package com.x.bridge.proxy.core;

import io.netty.channel.ChannelHandlerContext;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 回复者
 * @author AD
 * @date 2022/6/21 15:47
 */
public final class Replier {
    
    private final ChannelHandlerContext ctx;
    
    private final ProxyContext proxy;
    
    private final Queue<Message> proxyMessages;
    
    private final Queue<Message> appMessages;
    
    private long sendSeq;
    
    private long recvSeq;
    
    public Replier(ChannelHandlerContext ctx, ProxyContext proxy) {
        this.ctx = ctx;
        this.proxy = proxy;
        this.proxyMessages = new LinkedBlockingQueue<>();
        this.appMessages = new LinkedBlockingQueue<>();
    }
    
    public void send(Message msg) {
    }
    
    public void receive(Message msg) {
        String cmd = msg.getCmd();
        
    }
    
    public void close() {
        if (ctx != null) {
            ctx.close();
        }
    }
    
    public boolean isOpen() {
        if (ctx != null) {
            return ctx.channel().isOpen();
        }
        return false;
    }
    
    public Message buildMessage(String cmd, byte[] data) {
        Message msg = new Message(nextRecvSeq(), data, cmd);
        msg.setAppClient(proxy.getAppClient());
        msg.setAppHost(proxy.getAppHost());
        msg.setAppPort(proxy.getAppPort());
        msg.setProxyServer(proxy.getProxyServer());
        return msg;
    }
    
    private synchronized long nextSendSeq() {
        return sendSeq++;
    }
    
    private synchronized long nextRecvSeq() {
        return recvSeq++;
    }
    
}
