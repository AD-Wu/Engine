package com.x.bridge.proxy.session;

import com.google.common.base.Objects;
import com.x.bridge.bean.Message;
import com.x.bridge.proxy.command.core.Command;
import com.x.bridge.netty.core.SocketConfig;
import com.x.bridge.netty.factory.SocketClient;
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
        this.connected = false;
        this.sends = new LinkedBlockingQueue<>();
        this.receives = new ConcurrentHashMap<>();
        resetSendSeq();
        resetRecvSeq();
    }

    void send(@Nonnull Command cmd, byte[] data) {
        Message msg = buildMessage(cmd, data, nextSendSeq());
        sends.add(msg);
    }

    public void receive(Message... msgs) {
        for (int i = 0; i < msgs.length; i++) {
            Message msg = msgs[i];
            receives.put(msg.getSeq(), msg);
        }
        Message next = receives.remove(nextRecv);
        if (next != null) {
            execute(next);
        }
    }

    private void execute(Message msg) {
        if (nextRecv == msg.getSeq()) {
            synchronized (this) {
                if (nextRecv == msg.getSeq()) {
                    switch (Command.get(msg.getCmdCode())) {
                        case open:
                            log.info("代理【{}】发来会话【{}】建立命令", msg.getAgentServer(), appClient);
                            SocketConfig conf = SocketConfig.getClientConfig(msg.getAppHost(), msg.getAppPort());
                            SocketClient client = new SocketClient(appClient, conf, new ClientListener(manager, appClient));
                            client.start();
                            break;
                        case data:
                            chn.write(msg.getData());
                            log.info("会话【{}】发送第【{}】条数据【{}】", appClient, msg.getSeq(), msg.getData().length);
                            break;
                        case close:
                        case timeout:
                            close();
                            break;
                        case openSuccess:
                            connected = true;
                            break;
                        case openFail:
                            connected = false;
                            close();
                            break;
                        default:
                            break;
                    }
                }
                nextRecvSeq();
            }
        }
        Message next = receives.remove(nextRecv);
        if (next != null) {
            execute(next);
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

    boolean isConnected() {
        return this.connected;
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
