package com.x.bridge.proxy.core.socket;

import com.google.common.base.Objects;
import com.x.bridge.proxy.cmd.ISessionCmd;
import com.x.bridge.proxy.cmd.SessionCmd;
import com.x.bridge.bean.SessionMessage;
import com.x.bridge.proxy.core.IProxyService;
import com.x.doraemon.Strings;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author AD
 * @date 2022/7/14 15:02
 */
@Log4j2
public class Session {

    public static final long seq = 1;

    private final String appClient;

    private final IProxyService proxy;

    private final Map<Long, SessionMessage> receives;

    private ChannelHandlerContext chn;

    private volatile boolean connected;

    private long nextSend;

    private long nextRecv;

    public Session(String appClient, IProxyService proxy) {
        this.appClient = appClient;
        this.proxy = proxy;
        this.receives = new ConcurrentHashMap<>();
        this.connected = false;
        resetSendSeq();
        resetRecvSeq();
    }

    public void sendToProxy(int cmd, byte[] data) {
        SessionMessage msg = buildMessage(cmd, data);
        try {
            proxy.getBus().write(msg);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("写入数据失败,异常原因:{}", Strings.getExceptionTrace(e));
        }
    }

    public void sendToApp(byte[] data) {
        chn.write(data);
    }

    public void receive(SessionMessage... msgs) {
        for (int i = 0; i < msgs.length; i++) {
            SessionMessage msg = msgs[i];
            if (msg.getSeq() >= nextRecv) {
                receives.put(msg.getSeq(), msg);
            }
        }
        SessionMessage next = receives.remove(nextRecv);
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

    public IProxyService getProxy() {
        return proxy;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    void setChannel(ChannelHandlerContext chn) {
        this.chn = chn;
    }

    private void execute(SessionMessage msg) {
        if (nextRecv == msg.getSeq()) {
            synchronized (this) {
                if (nextRecv == msg.getSeq()) {
                    ISessionCmd cmd = SessionCmd.get(msg.getCmd());
                    cmd.execute(msg, this, proxy);
                }
                nextRecvSeq();
            }
        }
        SessionMessage next = receives.remove(nextRecv);
        if (next != null) {
            execute(next);
        }
    }

    private SessionMessage buildMessage(int cmd, byte[] data) {
        if (SessionCmd.get(cmd) == null) {
            throw new RuntimeException("非法命令【" + cmd + "】");
        }
        SessionMessage msg = new SessionMessage();
        msg.setProxyName(proxy.config().getName());
        msg.setClient(appClient);
        msg.setCmd(cmd);
        msg.setSeq(nextSendSeq());
        msg.setData(data);

        msg.setAppHost(proxy.config().getAppHost());
        msg.setAppPort(proxy.config().getAppPort());
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
        this.nextSend = seq;
    }

    private void resetRecvSeq() {
        this.nextRecv = seq;
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
