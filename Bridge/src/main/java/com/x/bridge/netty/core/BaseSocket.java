package com.x.bridge.netty.core;

import com.x.bridge.netty.interfaces.ISocket;
import com.x.bridge.netty.interfaces.ISessionListener;
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
public abstract class BaseSocket implements ISocket {

    protected final SocketConfig config;

    protected final ISessionListener listener;

    protected volatile boolean running = false;

    protected Channel channel;

    protected EventLoopGroup worker;

    protected BaseSocket(SocketConfig config, ISessionListener listener) {
        this.config = config;
        this.listener = listener;
    }

    @Override
    public final boolean start() {
        if (!running) {
            try {
                running = true;
                onStart();
            } catch (Exception e) {
                e.printStackTrace();
                stop();
            }
        }
        return running;
    }

    @Override
    public final void stop() {
        if (running) {
            if (channel != null) {
                channel.close();
            }
            if (worker != null) {
                worker.shutdownGracefully();
            }
            try {
                onStop();
            } catch (Exception e) {
                e.printStackTrace();
            }
            running = false;
        }
    }

    protected abstract void onStart() throws Exception;

    protected void onStop() throws Exception{}

    /**
     * socket 通道处理器
     */
    public static final class NettyChannelHandler extends SimpleChannelInboundHandler<ByteBuf> {

        private final ISessionListener listener;

        public NettyChannelHandler(ISessionListener listener) {
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
