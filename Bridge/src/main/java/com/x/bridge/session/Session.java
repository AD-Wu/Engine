package com.x.bridge.session;

import com.google.common.base.Objects;
import com.x.bridge.bean.Message;
import com.x.bridge.enums.Command;
import com.x.bridge.enums.MessageType;
import com.x.bridge.proxy.core.ICommand;
import com.x.bridge.proxy.core.IProxy;
import com.x.doraemon.Strings;
import io.netty.channel.ChannelHandlerContext;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.log4j.Log4j2;

/**
 * @author AD
 * @date 2022/7/14 15:02
 */
@Log4j2
public class Session {

    private final String appClient;

    private final IProxy proxy;

    private final Map<Long, Message> receives;

    private ChannelHandlerContext chn;

    private volatile boolean connected;

    private long nextSend;

    private long nextRecv;

    public Session(String appClient, IProxy proxy) {
        this.appClient = appClient;
        this.proxy = proxy;
        this.receives = new ConcurrentHashMap<>();
        this.connected = false;
        resetSendSeq();
        resetRecvSeq();
    }

    public void sendToProxy(int cmd, byte[] data) {
        Message msg = buildMessage(cmd, data);
        try {
            proxy.getTransporter().write(msg);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("写入数据失败,异常原因:{}", Strings.getExceptionTrace(e));
        }
    }

    public void sendToApp(byte[] data) {
        chn.write(data);
    }

    public void receive(Message... msgs) {
        for (int i = 0; i < msgs.length; i++) {
            Message msg = msgs[i];
            if (msg.getSeq() >= nextRecv) {
                receives.put(msg.getSeq(), msg);
            }
        }
        Message next = receives.remove(nextRecv);
        if (next != null) {
            execute(next);
        }
    }

    public void close() {
        if (chn != null && chn.channel().isOpen()) {
            chn.close();
            receives.clear();
        }
    }

    public IProxy getProxy() {
        return proxy;
    }

    public void setChannel(ChannelHandlerContext chn) {
        this.chn = chn;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    private void execute(Message msg) {
        if (nextRecv == msg.getSeq()) {
            synchronized (this) {
                if (nextRecv == msg.getSeq()) {
                    ICommand cmd = Command.get(msg.getCmd());
                    cmd.execute(msg, this, proxy);
                }
                nextRecvSeq();
            }
        }
        Message next = receives.remove(nextRecv);
        if (next != null) {
            execute(next);
        }
    }

    private Message buildMessage(int cmd, byte[] data) {
        if (Command.get(cmd) == null) {
            throw new RuntimeException("非法命令【" + cmd + "】");
        }
        Message msg = new Message();
        msg.setSeq(nextSendSeq());
        msg.setType(MessageType.socket.code);
        msg.setCmd(cmd);
        msg.setData(data);
        msg.setAppClient(appClient);
        msg.setAppHost(proxy.getConfig().getAppHost());
        msg.setAppPort(proxy.getConfig().getAppPort());
        msg.setProxyName(proxy.getConfig().getName());
        return msg;
    }

    private synchronized long nextSendSeq() {
        if (nextSend == Long.MAX_VALUE) {
            resetSendSeq();
        }
        return nextSend++;
    }

    private synchronized void nextRecvSeq() {
        ++nextRecv;
        if (nextRecv == Long.MAX_VALUE) {
            resetRecvSeq();
        }
    }

    private void resetSendSeq() {
        this.nextSend = 1;
    }

    private void resetRecvSeq() {
        this.nextRecv = 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Session session = (Session) o;
        return Objects.equal(appClient, session.appClient);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(appClient);
    }
}
