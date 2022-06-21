package com.x.bridge.netty.factory;

import com.x.bridge.netty.core.Netty;
import com.x.bridge.netty.interfaces.INettyListener;
import com.x.bridge.netty.core.NettyConfig;
import com.x.doraemon.Strings;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.TimeUnit;

/**
 * socket客户端
 * @author AD
 * @date 2022/6/21 13:08
 */
@Log4j2
public class NettyClient extends Netty {
    
    public NettyClient(String name, NettyConfig config, INettyListener listener) {
        super(name, config, listener);
    }
    
    @Override
    public void run() throws Exception {
        worker = new NioEventLoopGroup(1);
        Bootstrap boot = new Bootstrap();
        boot.group(worker);
        boot.channel(NioSocketChannel.class);
        boot.option(ChannelOption.SO_KEEPALIVE, true);
    
        boot.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
                ChannelPipeline p = channel.pipeline();
                // 设置超时时间
                p.addLast(new IdleStateHandler(
                        config.getReadTimeout(),
                        config.getWriteTimeout(),
                        config.getIdleTimeout(),
                        TimeUnit.MINUTES
                ));
                // 设置socket通道监听器
                p.addLast(new NettyChannelHandler(listener));
            }
        });
    
        try {
            ChannelFuture future = boot.connect(config.getHost(), config.getPort()).sync();
            channel = future.channel();
            running = true;
        } catch (InterruptedException e) {
            log.error(Strings.getExceptionTrace(e));
            Thread.currentThread().interrupt();
            stop();
        }
    }
    
    @Override
    public boolean isServerMode() {
        return false;
    }
    
}
