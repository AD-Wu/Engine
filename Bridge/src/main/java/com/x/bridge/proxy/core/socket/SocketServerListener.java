package com.x.bridge.proxy.core.socket;

/**
 * @author AD
 * @date 2022/7/12 14:54
 */

import com.x.bridge.netty.interfaces.ISessionListener;
import com.x.bridge.proxy.cmd.SessionCmd;
import com.x.bridge.proxy.core.IProxyService;
import com.x.bridge.proxy.enums.ProxyStatus;
import com.x.bridge.proxy.enums.BusStatus;
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
public class SocketServerListener implements ISessionListener {

    private final IProxyService proxy;

    public SocketServerListener(IProxyService proxy) {
        this.proxy = proxy;
    }

    @Override
    public void active(ChannelHandlerContext ctx) throws Exception {
        ProxyStatus status = proxy.status();
        BusStatus tranStatus = proxy.getBus().status();
        if (status == ProxyStatus.syncStart) {
            log.info("代理【{}】同步启动中... , 连接将被关闭", proxy.name());
            ctx.close();
            return;
        }
        if (status == ProxyStatus.running && tranStatus == BusStatus.running) {
            ChannelInfo ci = ChannelHelper.getChannelInfo(ctx);
            if (proxy.isAccept(ci.getRemoteHost())) {
                log.info("代理【{}】连接建立【{}】,同步连接中...", proxy.name(), ci.getRemote());
                Session session = new Session(ci.getRemote(), proxy);
                session.setChannel(ctx);
                proxy.putSession(ci.getRemote(), session);
                session.sendToProxy(SessionCmd.open.code, null);
            } else {
                log.warn("代理【{}】非法客户端连接【{}】", proxy.name(), ci.getRemote());
            }
        } else {
            log.info("代理【{}】状态【{}】，传输者状态【{}】，无法接受连接，连接将被关闭",
                    proxy.name(), status, tranStatus);
            ctx.close();
            return;
        }

    }

    @Override
    public void inActive(ChannelHandlerContext ctx) throws Exception {
        ChannelInfo ci = ChannelHelper.removeChannelInfo(ctx);
        Session session = proxy.removeSession(ci.getRemote());
        if (session != null) {
            if (session.isConnected()) {
                log.info("代理【{}】连接关闭【{}】,通知另一端代理关闭", proxy.name(), ci.getRemote());
                session.sendToProxy(SessionCmd.close.code, null);
            } else {
                log.info("代理【{}】连接关闭【{}】,无需通知另一端代理", proxy.name(), ci.getRemote());
            }
        }
    }

    @Override
    public void receive(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
        ChannelInfo ci = ChannelHelper.getChannelInfo(ctx);
        Session session = proxy.getSession(ci.getRemote());
        if (session != null) {
            byte[] data = ChannelHelper.readData(buf);
            if (data != null && data.length > 0) {
                session.sendToProxy(SessionCmd.data.code, data);
            }
        }
    }

    @Override
    public void timeout(ChannelHandlerContext ctx, IdleStateEvent event) throws Exception {
        ChannelInfo ci = ChannelHelper.removeChannelInfo(ctx);
    }

    @Override
    public void error(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ChannelInfo ci = ChannelHelper.removeChannelInfo(ctx);
    }

}
