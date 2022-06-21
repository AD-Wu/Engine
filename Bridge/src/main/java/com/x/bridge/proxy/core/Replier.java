package com.x.bridge.proxy.core;

import io.netty.channel.ChannelHandlerContext;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 回复者
 * @author AD
 * @date 2022/6/21 15:47
 */
public class Replier {
    
    private String remote;
    
    private String local;
    
    private volatile ChannelHandlerContext ctx;
    
    private final Queue<byte[]> proxyDatas;
    
    private final Queue<byte[]> targetDatas;
    
    public Replier(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        this.proxyDatas = new LinkedBlockingQueue<>();
        this.targetDatas = new LinkedBlockingQueue<>();
    }
    
    public void toProxy(byte[] data) {
        proxyDatas.offer(data);
    }
    
    public void toTarget(byte[] data){
        targetDatas.offer(data);
    }
    
    public void close() {
        if (ctx != null) {
            ctx.close();
            ctx = null;
        }
    }
    
    public String getRemote() {
        return remote;
    }
    
    public void setRemote(String remote) {
        this.remote = remote;
    }
    
    public String getLocal() {
        return local;
    }
    
    public void setLocal(String local) {
        this.local = local;
    }
    
}
