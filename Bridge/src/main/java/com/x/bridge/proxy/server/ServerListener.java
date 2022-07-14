package com.x.bridge.proxy.server;

/**
 * @author AD
 * @date 2022/7/12 14:54
 */

import com.x.bridge.netty.interfaces.ISessionListener;
import com.x.bridge.proxy.command.Command;
import com.x.bridge.proxy.core.IProxy;
import com.x.bridge.session.Session;
import com.x.bridge.util.ChannelHelper;
import com.x.bridge.util.ChannelInfo;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.log4j.Log4j2;

/**
 * 代理服务端监听器
 */
@Log4j2
public class ServerListener implements ISessionListener {

    private final IProxy proxy;

    public ServerListener(IProxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public void active(ChannelHandlerContext chn) throws Exception {
        ChannelInfo ci = ChannelHelper.getChannelInfo(chn);
        if (proxy.isAccept(ci.getRemoteHost())) {
            log.info("代理【{}】连接建立【{}】,同步连接中...", proxy.name(), ci.getRemote());
            Session session = new Session(ci.getRemote(), proxy);
            session.setChannel(chn);
            proxy.getSessionManager().putSession(ci.getRemote(), session);
            session.sendToProxy(Command.open.code, null);
        } else {
            log.warn("代理【{}】非法客户端连接【{}】", proxy.name(), ci.getRemote());
        }
    }

    @Override
    public void inActive(ChannelHandlerContext chn) throws Exception {
        ChannelInfo ci = ChannelHelper.getChannelInfo(chn);
        Session session = proxy.getSessionManager().removeSession(ci.getRemote());
        if (session != null) {
            if (session.isConnected()) {
                log.info("代理【{}】连接关闭【{}】,通知另一端代理关闭", proxy.name(), ci.getRemote());
                session.sendToProxy(Command.close.code, null);
            } else {
                log.info("代理【{}】连接关闭【{}】,无需通知另一端代理", proxy.name(), ci.getRemote());
            }
        }
    }

    @Override
    public void receive(ChannelHandlerContext chn, ByteBuf buf) throws Exception {
        ChannelInfo ci = ChannelHelper.getChannelInfo(chn);
        Session session = proxy.getSessionManager().getSession(ci.getRemote());
        if (session != null) {
            byte[] data = ChannelHelper.readData(buf);
            if (data != null && data.length > 0) {
                session.sendToProxy(Command.data.code, data);
            }
        }
    }

    @Override
    public void timeout(ChannelHandlerContext ctx, IdleStateEvent event) throws Exception {

    }

    @Override
    public void error(ChannelHandlerContext ctx, Throwable cause) throws Exception {

    }

}
