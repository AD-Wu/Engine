package com.x.bridge.netty.core;

import com.x.bridge.netty.interfaces.INetty;
import com.x.bridge.netty.interfaces.INettyListener;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * socket服务超类
 * @author AD
 * @date 2022/6/21 12:09
 */
public abstract class Netty implements INetty {
    
    protected final String name;
    
    protected final NettyConfig config;
    
    protected final INettyListener listener;
    
    protected boolean running = false;
    
    protected Channel channel;
    
    protected EventLoopGroup worker;
    
    protected Netty(String name, NettyConfig config, INettyListener listener) {
        this.name = name;
        this.config = config;
        this.listener = listener;
    }
    
    @Override
    public String name() {
        return name;
    }
    
    @Override
    public final synchronized boolean start() {
        if (!running) {
            try {
                run();
                running = true;
            } catch (Exception e) {
                e.printStackTrace();
                stop();
            }
        }
        return running;
    }
    
    @Override
    public final synchronized void stop() {
        if (running) {
            if (channel != null) {
                channel.close();
            }
            if (worker != null) {
                worker.shutdownGracefully();
            }
            shutdown();
            running = false;
        }
    }
    
    protected abstract void run() throws Exception;
    
    protected void shutdown() {}
    
    /**
     * socket 通道处理器
     */
    public static final class NettyChannelHandler extends SimpleChannelInboundHandler<ByteBuf> {
        
        private final INettyListener listener;
        
        public NettyChannelHandler(INettyListener listener) {
            this.listener = listener;
        }
        
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            listener.active(ctx);
        }
        
        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            listener.inActive(ctx);
        }
        
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            listener.receive(ctx, msg);
        }
        
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent event = (IdleStateEvent) evt;
                if (IdleState.ALL_IDLE == event.state()) {
                    listener.timeout(ctx, event);
                }
            }
        }
        
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            listener.error(ctx, cause);
        }
        
    }
    
}
