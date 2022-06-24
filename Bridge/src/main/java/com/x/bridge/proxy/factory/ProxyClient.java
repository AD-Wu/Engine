package com.x.bridge.proxy.factory;

import com.x.bridge.netty.core.NettyConfig;
import com.x.bridge.netty.factory.NettyClient;
import com.x.bridge.netty.interfaces.INetty;
import com.x.bridge.netty.interfaces.INettyListener;
import com.x.bridge.proxy.ProxyManager;
import com.x.bridge.proxy.core.Command;
import com.x.bridge.proxy.core.ProxyContext;
import com.x.bridge.proxy.core.Replier;
import com.x.bridge.proxy.interfaces.IProxy;
import com.x.bridge.util.ChannelHelper;
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
    
    private final ProxyContext ctx;
    
    private final String proxyServer;
    
    private final INetty client;
    
    private Replier replier;
    
    public ProxyClient(ProxyContext ctx) {
        this.ctx = ctx;
        this.proxyServer = ctx.getProxyServer();
        NettyConfig conf = NettyConfig.getClientConfig(ctx.getAppHost(), ctx.getAppPort());
        this.client = new NettyClient(name(), conf, new ClientListener());
    }
    
    @Override
    public String name() {
        return ctx.getAppClient();
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
        public void active(ChannelHandlerContext chn) throws Exception {
            log.info("连接建立:{}", ctx.getAppClient());
            ProxyManager.putProxyClient(proxyServer, ctx.getAppClient(), ProxyClient.this);
            replier = new Replier(chn, ctx);
            replier.send(replier.buildMessage(Command.OPEN_SUCCESS.toString(), null));
            
        }
        
        @Override
        public void inActive(ChannelHandlerContext chn) throws Exception {
            ProxyManager.closeProxyClient(proxyServer, ctx.getAppClient());
            if (replier != null) {
                replier.close();
                replier.send(replier.buildMessage(Command.CLOSE.toString(), null));
                log.info("连接关闭:【{}】", ctx.getAppClient());
            }
        }
        
        @Override
        public void receive(ChannelHandlerContext chn, ByteBuf buf) throws Exception {
            if (replier != null) {
                byte[] data = ChannelHelper.readData(buf);
                replier.send(replier.buildMessage(Command.DATA.toString(), data));
            }
        }
        
        @Override
        public void timeout(ChannelHandlerContext chn, IdleStateEvent event) throws Exception {
            if (replier != null) {
                replier.close();
            }
            replier.send(replier.buildMessage(Command.TIMEOUT.toString(), null));
            log.info("连接超时:【{}】", ctx.getAppClient());
        }
        
        @Override
        public void error(ChannelHandlerContext chn, Throwable cause) throws Exception {
            if (replier != null) {
                replier.close();
            }
            replier.send(replier.buildMessage(Command.OPEN_FAIL.toString(), null));
            log.info("连接错误:【{}】", ctx.getAppClient());
        }
        
    }
    
}
