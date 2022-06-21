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
    
    void active(ChannelHandlerContext ctx) throws Exception;
    
    void inActive(ChannelHandlerContext ctx) throws Exception;
    
    void receive(ChannelHandlerContext ctx, ByteBuf buf) throws Exception;
    
    void timeout(ChannelHandlerContext ctx, IdleStateEvent event) throws Exception;
    
    void error(ChannelHandlerContext ctx, Throwable cause) throws Exception;
    
}
