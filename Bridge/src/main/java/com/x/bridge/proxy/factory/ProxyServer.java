package com.x.bridge.proxy.factory;

import com.x.bridge.netty.factory.NettyServer;
import com.x.bridge.netty.interfaces.INetty;
import com.x.bridge.netty.interfaces.INettyListener;
import com.x.bridge.proxy.core.ProxyConfig;
import com.x.bridge.proxy.core.Replier;
import com.x.bridge.proxy.interfaces.IProxy;
import com.x.bridge.util.ChannelHelper;
import com.x.bridge.util.ChnInfo;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;

/**
 * 代理服务器
 * @author AD
 * @date 2022/6/21 14:48
 */
@Log4j2
public class ProxyServer implements IProxy {
    
    // ------------------------ 变量定义 ------------------------
    
    private final ProxyConfig conf;
    
    private final Map<String, Replier> repliers;
    
    private final INetty server;
    
    // ------------------------ 构造方法 ------------------------
    
    public ProxyServer(ProxyConfig conf) {
        this.conf = conf;
        this.repliers = new HashMap<>();
        this.server = new NettyServer(conf.getName(), conf.getProxyPort(), new ServerListener());
    }
    
    // ------------------------ 方法定义 ------------------------
    
    @Override
    public String name() {
        return server.name();
    }
    
    @Override
    public boolean isServerMode() {
        return true;
    }
    
    @Override
    public boolean start() {
        return server.start();
    }
    
    @Override
    public void stop() {
        server.stop();
    }
    
    @Override
    public boolean isAccept(String host) {
        return conf.getAllowClients().contains(host);
    }
    
    // ------------------------ 内部类 ------------------------
    
    /**
     * 代理服务端监听器
     */
    private class ServerListener implements INettyListener {
        
        @Override
        public void active(ChannelHandlerContext ctx) throws Exception {
            ChnInfo chn = ChannelHelper.getChannelInfo(ctx);
            if (isAccept(chn.getRemoteHost())) {
                Replier replier = new Replier(ctx);
                replier.setRemote(chn.getRemote());
                replier.setLocal(chn.getLocal());
                addReplier(replier);
            } else {
                log.warn("代理:【{}】非法客户端连接:【{}】", name(), chn.getRemote());
            }
        }
        
        @Override
        public void inActive(ChannelHandlerContext ctx) throws Exception {
            ChnInfo chn = ChannelHelper.getChannelInfo(ctx);
            Replier replier = removeReplier(chn.getRemoteHost());
            if (replier != null) {
                replier.close();
                log.info("代理:【{}】连接关闭:【{}】", name(), replier.getRemote());
            }
        }
        
        @Override
        public void receive(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
            ChnInfo chn = ChannelHelper.getChannelInfo(ctx);
            Replier replier = getReplier(chn.getRemote());
            if (replier != null) {
                byte[] data = ChannelHelper.readData(buf);
                replier.toProxy(data);
            }
        }
        
        @Override
        public void timeout(ChannelHandlerContext ctx, IdleStateEvent event) throws Exception {
        
        }
        
        @Override
        public void error(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        
        }
        
    }
    
    // ------------------------ 私有方法 ------------------------
    
    private synchronized void addReplier(Replier replier) {
        String client = replier.getRemote();
        if (!repliers.containsKey(client)) {
            repliers.put(client, replier);
            log.info("代理:【{}】建立连接:【{}】", name(), client);
        } else {
            log.info("代理:【{}】已存在该连接:【{}】", name(), client);
        }
    }
    
    private synchronized Replier getReplier(String appClient) {
        return repliers.get(appClient);
    }
    
    private synchronized Replier removeReplier(String appClient) {
        return repliers.remove(appClient);
    }
    
}
