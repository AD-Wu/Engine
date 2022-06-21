package com.x.bridge.proxy.factory;

import com.x.bridge.netty.core.NettyConfig;
import com.x.bridge.netty.factory.NettyClient;
import com.x.bridge.netty.interfaces.INetty;
import com.x.bridge.netty.interfaces.INettyListener;
import com.x.bridge.proxy.core.Replier;
import com.x.bridge.proxy.interfaces.IProxy;
import com.x.bridge.util.ChannelHelper;
import com.x.bridge.util.ChnInfo;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.log4j.Log4j2;

/**
 * 代理客户端
 * @author AD
 * @date 2022/6/21 14:48
 */
@Log4j2
public class ProxyClient implements IProxy {
    
    private final String appServer;
    
    private final INetty client;
    
    private Replier replier;
    
    public ProxyClient(String host, int port) {
        this.appServer = host + ":" + port;
        NettyConfig conf = NettyConfig.getClientConfig(host, port);
        this.client = new NettyClient(appServer, conf, new ClientListener());
    }
    
    @Override
    public String name() {
        return appServer;
    }
    
    @Override
    public boolean isServerMode() {
        return false;
    }
    
    @Override
    public boolean start() {
        return client.start();
    }
    
    @Override
    public void stop() {
        client.stop();
    }
    
    
    /**
     * 代理客户端监听器
     */
    private class ClientListener implements INettyListener {
        
        @Override
        public void active(ChannelHandlerContext ctx) throws Exception {
            ChnInfo chn = ChannelHelper.getChannelInfo(ctx);
            replier = new Replier(ctx);
            replier.setLocal(chn.getLocal());
            replier.setRemote(chn.getRemote());
        }
        
        @Override
        public void inActive(ChannelHandlerContext ctx) throws Exception {
            ChnInfo chn = ChannelHelper.getChannelInfo(ctx);
            if (replier != null) {
                replier.close();
                log.info("连接关闭:【{}】", replier.getLocal());
            }
        }
        
        @Override
        public void receive(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
            ChnInfo chn = ChannelHelper.getChannelInfo(ctx);
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
    
}
