package com.x.bridge.netty.interfaces;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * socket监听器
 * @author AD
 * @date 2022/6/21 12:11
 */
public interface INettyListener {
    
    void active(ChannelHandlerContext chn) throws Exception;
    
    void inActive(ChannelHandlerContext chn) throws Exception;
    
    void receive(ChannelHandlerContext chn, ByteBuf buf) throws Exception;
    
    void timeout(ChannelHandlerContext chn, IdleStateEvent event) throws Exception;
    
    void error(ChannelHandlerContext chn, Throwable cause) throws Exception;
    
}
