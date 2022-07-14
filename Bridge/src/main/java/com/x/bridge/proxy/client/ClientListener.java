package com.x.bridge.proxy.client;

/**
 * @author AD
 * @date 2022/7/12 14:41
 */

import com.x.bridge.netty.interfaces.ISessionListener;
import com.x.bridge.proxy.command.Command;
import com.x.bridge.proxy.core.IProxy;
import com.x.bridge.session.Session;
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

    private final String appClient;

    private final IProxy proxy;

    public ClientListener(String appClient, IProxy proxy) {
        this.appClient = appClient;
        this.proxy = proxy;
    }

    @Override
    public void active(ChannelHandlerContext chn) throws Exception {
        log.info("连接建立:{}", appClient);
        Session session = proxy.getSessionManager().getSession(appClient);
        session.setChannel(chn);
        session.setConnected(true);
        session.sendToProxy(Command.openSuccess.code, null);
    }

    @Override
    public void inActive(ChannelHandlerContext chn) throws Exception {
        log.info("代理【{}】连接关闭【{}】", proxy.name(), appClient);
        Session session = proxy.getSessionManager().removeSession(appClient);
        if (session != null) {
            if (session.isConnected()) {
                log.info("代理【{}】连接关闭【{}】,通知另一端代理关闭", proxy.name(), appClient);
                session.sendToProxy(Command.close.code, null);
            } else {
                log.info("代理【{}】连接关闭【{}】,无需通知另一端代理", proxy.name(), appClient);
            }
        }
    }

    @Override
    public void receive(ChannelHandlerContext chn, ByteBuf buf) throws Exception {
        Session session = proxy.getSessionManager().getSession(appClient);
        if (session != null) {
            byte[] data = ChannelHelper.readData(buf);
            session.sendToProxy(Command.data.code, data);
        }
    }

    @Override
    public void timeout(ChannelHandlerContext chn, IdleStateEvent event) throws Exception {
        Session session = proxy.getSessionManager().removeSession(appClient);
        if (session != null) {
            session.sendToProxy(Command.timeout.code, null);
            session.close();
            log.info("连接超时:【{}】", appClient);
        }
    }

    @Override
    public void error(ChannelHandlerContext chn, Throwable cause) throws Exception {
        Session session = proxy.getSessionManager().removeSession(appClient);
        if (session != null) {
            session.sendToProxy(Command.openFail.code, null);
            session.close();
            log.info("连接错误:【{}】", appClient);
        }
    }

}
