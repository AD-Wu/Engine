package com.x.bridge.proxy.session;

/**
 * @author AD
 * @date 2022/7/12 14:41
 */

import com.x.bridge.enums.Command;
import com.x.bridge.netty.interfaces.ISessionListener;
import com.x.bridge.proxy.interfaces.ISessionManager;
import com.x.bridge.util.ChannelHelper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.log4j.Log4j2;

/**
 * 代理客户端监听器
 */
@Log4j2
public class ClientListener implements ISessionListener {

    private final ISessionManager sessionManager;

    private final String appClient;

    public ClientListener(ISessionManager sessionManager, String appClient) {
        this.sessionManager = sessionManager;
        this.appClient = appClient;
    }

    @Override
    public void active(ChannelHandlerContext chn) throws Exception {
        log.info("连接建立:{}", appClient);
        Session session = sessionManager.getSession(appClient);
        session.setChannel(chn);
        session.setConnectSuccess(true);
        session.send(Command.openSuccess, null);
    }

    @Override
    public void inActive(ChannelHandlerContext chn) throws Exception {
        log.info("代理【{}】连接关闭【{}】", sessionManager.name(), appClient);
        Session session = sessionManager.closeSession(appClient);
        if (session != null) {
            if (session.isConnectSuccess()) {
                log.info("通知另一端代理关闭连接:【{}】", appClient);
                session.send(Command.close, null);
            } else {
                log.info("无需通知另一端代理关闭连接:【{}】", appClient);
            }
        }
    }

    @Override
    public void receive(ChannelHandlerContext chn, ByteBuf buf) throws Exception {
        Session session = sessionManager.getSession(appClient);
        if (session != null) {
            byte[] data = ChannelHelper.readData(buf);
            session.send(Command.data, data);
        }
    }

    @Override
    public void timeout(ChannelHandlerContext chn, IdleStateEvent event) throws Exception {
        Session session = sessionManager.closeSession(appClient);
        if (session != null) {
            session.send(Command.timeout, null);
            log.info("连接超时:【{}】", appClient);
        }

    }

    @Override
    public void error(ChannelHandlerContext chn, Throwable cause) throws Exception {
        Session session = sessionManager.closeSession(appClient);
        if (session != null) {
            session.send(Command.openFail, null);
            log.info("连接错误:【{}】", appClient);
        }
    }

}
