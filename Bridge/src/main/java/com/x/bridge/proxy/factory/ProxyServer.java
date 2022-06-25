package com.x.bridge.proxy.factory;

import com.x.bridge.bean.Message;
import com.x.bridge.netty.factory.NettyServer;
import com.x.bridge.netty.interfaces.INetty;
import com.x.bridge.netty.interfaces.INettyListener;
import com.x.bridge.enums.Command;
import com.x.bridge.proxy.conf.ProxyConfig;
import com.x.bridge.proxy.core.ProxyContext;
import com.x.bridge.proxy.core.Replier;
import com.x.bridge.proxy.interfaces.IProxy;
import com.x.bridge.transport.SenderManager;
import com.x.bridge.transport.interfaces.IReceiver;
import com.x.bridge.transport.interfaces.ISender;
import com.x.bridge.util.ChannelHelper;
import com.x.bridge.util.ChnInfo;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.log4j.Log4j2;

/**
 * 代理服务器
 * @author AD
 * @date 2022/6/21 14:48
 */
@Log4j2
public class ProxyServer implements IProxy, IReceiver<Message> {

    // ------------------------ 变量定义 ------------------------

    private final ProxyConfig conf;

    private final ProxyContext ctx;

    private final Map<String, Replier> repliers;

    private final ISender<Message> sender;

    private final INetty server;

    // ------------------------ 构造方法 ------------------------

    public ProxyServer(ProxyConfig conf) {
        this.conf = conf;
        this.ctx = new ProxyContext();
        initProxyContext();
        this.repliers = new HashMap<>();
        this.sender = SenderManager.newSender(conf.getSendMode());
        this.server = new NettyServer(conf.getName(), conf.getProxyPort(), new ServerListener());
    }

    // ------------------------ 方法定义 ------------------------

    @Override
    public String name() {
        return server.name();
    }

    @Override
    public boolean isServerMode() {
        return true;
    }

    @Override
    public boolean start() {
        return server.start();
    }

    @Override
    public void stop() {
        server.stop();
    }

    @Override
    public boolean isAccept(String host) {
        return conf.getAllowClients().contains(host);
    }

    @Override
    public void receive(Message... msgs) {
        for (Message msg : msgs) {
            String client = msg.getAppClient();
            Replier replier = getReplier(client);
            if (replier != null) {
                replier.receive(msg);
            }
        }
    }

    // ------------------------ 内部类 ------------------------

    /**
     * 代理服务端监听器
     */
    private class ServerListener implements INettyListener {

        @Override
        public void active(ChannelHandlerContext chn) throws Exception {
            ChnInfo ci = ChannelHelper.getChannelInfo(chn);
            if (isAccept(ci.getRemoteHost())) {
                Replier replier = new Replier(chn, ctx, ci.getRemote());
                addReplier(ci.getRemote(), replier);
                replier.send(replier.buildMessage(Command.OPEN.toString(), null));
            } else {
                log.warn("代理:【{}】非法客户端连接:【{}】", name(), ci.getRemote());
            }
        }

        @Override
        public void inActive(ChannelHandlerContext chn) throws Exception {
            ChnInfo ci = ChannelHelper.getChannelInfo(chn);
            Replier replier = removeReplier(ci.getRemote());
            if (replier != null) {
                replier.close();
                replier.send(replier.buildMessage(Command.CLOSE.toString(), null));
                log.info("代理:【{}】连接关闭:【{}】", name(), ci.getRemote());
            }
        }

        @Override
        public void receive(ChannelHandlerContext chn, ByteBuf buf) throws Exception {
            ChnInfo ci = ChannelHelper.getChannelInfo(chn);
            Replier replier = getReplier(ci.getRemote());
            if (replier != null) {
                byte[] data = ChannelHelper.readData(buf);
                replier.send(replier.buildMessage(Command.DATA.toString(), data));
            }
        }

        @Override
        public void timeout(ChannelHandlerContext ctx, IdleStateEvent event) throws Exception {

        }

        @Override
        public void error(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        }

    }

    // ------------------------ 私有方法 ------------------------

    private synchronized void addReplier(String client, Replier replier) {
        if (!repliers.containsKey(client)) {
            repliers.put(client, replier);
            log.info("代理:【{}】建立连接:【{}】", name(), client);
        } else {
            log.info("代理:【{}】已存在该连接:【{}】", name(), client);
        }
    }

    private synchronized Replier getReplier(String appClient) {
        return repliers.get(appClient);
    }

    private synchronized Replier removeReplier(String appClient) {
        return repliers.remove(appClient);
    }

    private void initProxyContext() {
        ctx.setAppHost(conf.getAppHost());
        ctx.setAppPort(conf.getAppPort());
        try {
            ctx.setProxyServer(InetAddress.getLocalHost().getHostAddress() + ":" + conf.getProxyPort());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

}
