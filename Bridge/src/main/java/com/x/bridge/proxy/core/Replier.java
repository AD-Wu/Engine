package com.x.bridge.proxy.core;

import com.x.bridge.bean.Message;
import com.x.bridge.proxy.command.CommandManager;
import com.x.bridge.proxy.command.interfaces.ICommand;
import com.x.bridge.proxy.interfaces.IListener;
import io.netty.channel.ChannelHandlerContext;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 回复者
 * @author AD
 * @date 2022/6/21 15:47
 */
public final class Replier {

    private final ChannelHandlerContext ctx;

    private final ProxyContext proxy;

    private final String appClient;

    private volatile boolean openConnect;

    private final Queue<Message> sends;

    private final Map<Long, Message> receives;

    private long nextSend;

    private long nextRecv;

    public Replier(ChannelHandlerContext ctx, ProxyContext proxy, String appClient) {
        this.ctx = ctx;
        this.proxy = proxy;
        this.appClient = appClient;
        this.openConnect = false;
        this.sends = new LinkedBlockingQueue<>();
        this.receives = new ConcurrentHashMap<>();
    }

    public void send(String cmd, IListener<Message> listener) {
        Message msg = buildMessage(cmd, null);

    }

    public void send(Message msg) {
        sends.add(msg);
    }

    public void receive(Message msg) {
        ICommand cmd = CommandManager.getCommand(msg.getCmd());
        if (cmd != null) {
            if (msg.getSeq() > nextRecv) {
                receives.put(msg.getSeq(), msg);
            } else {
                if (nextRecv == msg.getSeq()) {
                    synchronized (this) {
                        if (nextRecv == msg.getSeq()) {
                            cmd.execute(msg, this);
                            ++nextRecv;
                        }
                    }
                }
                Message next = receives.remove(nextRecv);
                if (next != null) {
                    receive(next);
                }
            }
        }
    }

    public void setOpenConnect(boolean openConnect) {
        this.openConnect = openConnect;
    }

    public void close() {
        if (ctx != null) {
            ctx.close();
        }
    }

    public boolean isOpen() {
        return ctx != null && ctx.channel().isOpen();
    }

    public Message buildMessage(String cmd, byte[] data) {
        Message msg = new Message();
        msg.setSeq(nextSendSeq());
        msg.setCmd(cmd);
        msg.setData(data);
        msg.setAppClient(appClient);
        msg.setAppHost(proxy.getAppHost());
        msg.setAppPort(proxy.getAppPort());
        msg.setProxyServer(proxy.getProxyServer());
        return msg;
    }

    private synchronized long nextSendSeq() {
        return nextSend++;
    }

}
