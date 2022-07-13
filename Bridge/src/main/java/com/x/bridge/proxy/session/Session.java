package com.x.bridge.proxy.session;

import com.x.bridge.bean.Message;
import com.x.bridge.enums.Command;
import com.x.bridge.proxy.data.AgentConfig;
import com.x.bridge.proxy.interfaces.ISessionManager;
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

    private final ISessionManager manager;

    private volatile boolean connected;

    private final Queue<Message> sends;

    private final Map<Long, Message> receives;

    private ChannelHandlerContext chn;

    private long nextSend = 1;

    private long nextRecv = 1;

    Session(String appClient, ISessionManager manager) {
        this(null, appClient, manager);
    }

    Session(ChannelHandlerContext chn, String appClient, ISessionManager manager) {
        this.chn = chn;
        this.appClient = appClient;
        this.manager = manager;
        this.connected = false;
        this.sends = new LinkedBlockingQueue<>();
        this.receives = new ConcurrentHashMap<>();
    }

    public void openConnect() {
        if (manager.isServerMode()) {
            Message msg = buildMessage(Command.open, null, 0);
            sends.add(msg);
        }
    }

    public void send(@Nonnull Command cmd, byte[] data) {
        Message msg = buildMessage(cmd, data, nextSendSeq());
        sends.add(msg);
    }

    public void receive(Message msg) {
        if (isConnected()) {
            if (msg.getSeq() > nextRecv) {
                receives.put(msg.getSeq(), msg);
            } else {
                if (nextRecv == msg.getSeq()) {
                    synchronized (this) {
                        if (nextRecv == msg.getSeq()) {
                            chn.write(msg.getData());
                            ++nextRecv;
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

    public void setConnected(boolean connected) {
        this.connected = connected;
        if (connected) {
            Message next = receives.remove(nextRecv);
            if (next != null) {
                receive(next);
            }
        }
    }

    public boolean isConnected() {
        return this.connected;
    }

    public void close() {
        if (chn != null) {
            chn.close();
            connected = false;
        }
    }

    void setChannel(ChannelHandlerContext chn) {
        if (chn != null) {
            this.chn = chn;
        }
    }

    private Message buildMessage(Command cmd, byte[] data, long seq) {
        Message msg = new Message();
        msg.setSeq(seq);
        msg.setCmd(cmd.toString());
        msg.setData(data);
        msg.setAppClient(appClient);

        AgentConfig conf = manager.getAgentConfig();
        msg.setAppHost(conf.getAppHost());
        msg.setAppPort(conf.getAppPort());
        msg.setAgentServer(AgentConfig.getAgentHost() + conf.getAgentPort());
        return msg;
    }

    private synchronized long nextSendSeq() {
        return nextSend++;
    }
}
