package com.x.bridge.netty.factory;

import com.x.bridge.netty.core.BaseSocket;
import com.x.bridge.netty.interfaces.ISessionListener;
import com.x.bridge.netty.core.SocketConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.TimeUnit;

/**
 * socket服务器
 * @author AD
 * @date 2022/6/21 12:49
 */
@Log4j2
public class SocketServer extends BaseSocket {

    private EventLoopGroup boss;

    public SocketServer(int port, ISessionListener listener) {
        this(SocketConfig.getServerConfig(port), listener);
    }

    public SocketServer(SocketConfig conf, ISessionListener listener) {
        super(conf, listener);
    }

    @Override
    protected void run() throws Exception {
        ServerBootstrap boot = new ServerBootstrap();
        boot.channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_BACKLOG, config.getBacklog())
                .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(config.getRecvBuf()));

        boot.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel channel) throws Exception {
                ChannelPipeline p = channel.pipeline();
                // 设置超时事件处理器
                p.addLast(new IdleStateHandler(
                        config.getReadTimeout(),
                        config.getWriteTimeout(),
                        config.getIdleTimeout(),
                        TimeUnit.MINUTES
                ));
                // 设置通道监听器
                p.addLast(new NettyChannelHandler(listener));
            }
        });
        // 创建线程池
        boss = new NioEventLoopGroup(config.getBossCount());
        worker = new NioEventLoopGroup(config.getWorkerCount());
        boot.group(boss, worker);
        ChannelFuture future = boot.bind(config.getPort()).sync();
        channel = future.channel();
    }

    @Override
    protected void shutdown() {
        if (boss != null) {
            boss.shutdownGracefully();
        }
    }

    @Override
    public boolean isServerMode() {
        return true;
    }

}
