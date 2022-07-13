package com.x.bridge.proxy.session;

import com.google.common.base.Objects;
import com.x.bridge.bean.Message;
import com.x.bridge.enums.Command;
import com.x.bridge.proxy.data.AgentConfig;
import io.netty.channel.ChannelHandlerContext;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import javax.annotation.Nonnull;
import lombok.extern.log4j.Log4j2;

/**
 * 回复者
 * @author AD
 * @date 2022/6/21 15:47
 */
@Log4j2
public final class Session {

    private final String appClient;

    private final SessionManager manager;

    private final Queue<Message> sends;

    private final Map<Long, Message> receives;

    private final Object lock;

    private volatile boolean connected;

    private ChannelHandlerContext chn;

    private long nextSend;

    private long nextRecv;

    Session(String appClient, SessionManager manager) {
        this(null, appClient, manager);
    }

    Session(ChannelHandlerContext chn, String appClient, SessionManager manager) {
        this.chn = chn;
        this.appClient = appClient;
        this.manager = manager;
        this.lock = new Object();
        this.connected = false;
        this.sends = new LinkedBlockingQueue<>();
        this.receives = new ConcurrentHashMap<>();
        resetSendSeq();
        resetRecvSeq();
    }

    public void openConnect() {
        if (manager.isServerMode()) {
            Message msg = buildMessage(Command.open, null, 0);
            sends.add(msg);
        }
    }

    public void closeConnect() {
        Message msg = buildMessage(Command.close, null, Long.MAX_VALUE);
        sends.add(msg);
    }

    void send(@Nonnull Command cmd, byte[] data) {
        Message msg = buildMessage(cmd, data, nextSendSeq());
        sends.add(msg);
    }

    public void receive(Message msg) {
        if (msg.getCmdCode() == Command.data.code) {
            if (isConnected()) {
                if (msg.getSeq() > nextRecv) {
                    receives.put(msg.getSeq(), msg);
                } else {
                    if (nextRecv == msg.getSeq()) {
                        synchronized (this) {
                            if (nextRecv == msg.getSeq()) {
                                chn.write(msg.getData());
                                nextRecvSeq();
                                log.info("会话【{}】发送第【{}】条数据【{}】", appClient, msg.getSeq(), msg.getData().length);
                            }
                        }
                    }
                    Message next = receives.remove(nextRecv);
                    if (next != null) {
                        receive(next);
                    }
                }
            } else {
                receives.put(msg.getSeq(), msg);
            }
        }
    }

    public void close() {
        if (chn != null) {
            synchronized (this) {
                if (chn != null) {
                    chn.close();
                    chn = null;
                }
            }
        }
    }

    public void setConnected(boolean connSuccess) {
        this.connected = connSuccess;
    }

    public boolean isConnected() {
        return this.connected;
    }

    public Object getLock() {
        return lock;
    }

    void setChannel(ChannelHandlerContext chn) {
        if (chn != null) {
            this.chn = chn;
        }
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

    private Message buildMessage(Command cmd, byte[] data, long seq) {
        Message msg = new Message();
        msg.setSeq(seq);
        msg.setCmdCode(cmd.code);
        msg.setData(data);
        msg.setAppClient(appClient);

        AgentConfig conf = manager.getAgentConfig();
        msg.setAppHost(conf.getAppHost());
        msg.setAppPort(conf.getAppPort());
        msg.setAgentServer(AgentConfig.getAgentHost() + conf.getAgentPort());
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

}
