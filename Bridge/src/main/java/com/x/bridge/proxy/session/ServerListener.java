package com.x.bridge.proxy.session;

/**
 * @author AD
 * @date 2022/7/12 14:54
 */

import com.x.bridge.proxy.command.core.Command;
import com.x.bridge.netty.interfaces.ISessionListener;
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

    private final SessionManager sessionManager;

    public ServerListener(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void active(ChannelHandlerContext chn) throws Exception {
        ChannelInfo ci = ChannelHelper.getChannelInfo(chn);
        if (sessionManager.isAccept(ci.getRemoteHost())) {
            log.info("代理【{}】连接建立【{}】,同步连接中...", sessionManager.name(), ci.getRemote());
            Session session = sessionManager.createSession(chn, ci.getRemote());
            sessionManager.putSession(ci.getRemote(), session);
            session.send(Command.open, null);
        } else {
            log.warn("代理【{}】非法客户端连接【{}】", sessionManager.name(), ci.getRemote());
        }
    }

    @Override
    public void inActive(ChannelHandlerContext chn) throws Exception {
        ChannelInfo ci = ChannelHelper.getChannelInfo(chn);
        Session session = sessionManager.removeSession(ci.getRemote());
        if (session != null) {
            if (session.isConnected()) {
                log.info("代理【{}】连接关闭【{}】,通知另一端代理关闭", sessionManager.name(), ci.getRemote());
                session.send(Command.close, null);
            } else {
                log.info("代理【{}】连接关闭【{}】,无需通知另一端代理", sessionManager.name(), ci.getRemote());
            }
        }
    }

    @Override
    public void receive(ChannelHandlerContext chn, ByteBuf buf) throws Exception {
        ChannelInfo ci = ChannelHelper.getChannelInfo(chn);
        Session session = sessionManager.getSession(ci.getRemote());
        if (session != null) {
            byte[] data = ChannelHelper.readData(buf);
            if (data != null && data.length > 0) {
                session.send(Command.data, data);
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
