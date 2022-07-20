package com.x.bridge.proxy.core;

import static java.util.stream.Collectors.groupingBy;

import com.x.bridge.bean.Message;
import com.x.bridge.bean.SessionMessage;
import com.x.bridge.netty.common.Service;
import com.x.bridge.proxy.ProxyManager;
import com.x.bridge.proxy.cmd.Cmd;
import com.x.bridge.proxy.core.socket.Session;
import com.x.bridge.proxy.enums.MsgType;
import com.x.bridge.proxy.enums.ProxyStatus;
import com.x.bridge.proxy.enums.BusType;
import com.x.bridge.bus.core.IReader;
import com.x.bridge.bus.core.IBus;
import com.x.bridge.bus.core.IWriter;
import com.x.bridge.bus.core.MessageBus;
import com.x.doraemon.Strings;
import com.x.doraemon.therad.BalanceExecutor;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import lombok.extern.log4j.Log4j2;

/**
 * @author AD
 * @date 2022/7/14 17:02
 */
@Log4j2
public abstract class ProxyService extends Service implements IProxyService {

    protected final ProxyConfig conf;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private final Map<String, Session> sessions = new HashMap<>();

    private final BalanceExecutor<String> executor = new BalanceExecutor<>(name());

    protected volatile ProxyStatus status = ProxyStatus.creating;

    protected IBus bus;

    public ProxyService(ProxyConfig conf) {
        this.conf = conf;
        IReader reader = BusType.get(conf.getReadMode()).createReader(this);
        IWriter writer = BusType.get(conf.getWriteMode()).createWriter(this);
        this.bus = new MessageBus(this, reader, writer);
    }

    @Override
    public String name() {
        return conf.getName();
    }

    @Override
    public ProxyStatus status() {
        return status;
    }

    @Override
    public void status(ProxyStatus status) {
        this.status = status;
    }

    @Override
    public IBus getBus() {
        return bus;
    }

    @Override
    public ProxyConfig config() {
        return conf;
    }

    @Override
    protected void onStartError(Throwable e) {
        stop();
    }

    @Override
    public void sync(boolean isStart) {
        if (isStart) {
            status = ProxyStatus.syncStart;
        } else {
            status = ProxyStatus.syncSession;
        }
        String clients = Strings.joining(",", sessionKeys());
        byte[] clientsBytes = null;
        if (clients != null && clients.length() > 0) {
            clientsBytes = clients.getBytes(StandardCharsets.UTF_8);
        }
        Message msg = new Message();
        msg.setProxyName(name());
        msg.setCmd(Cmd.sync.code);
        msg.setType(MsgType.req.code);
        msg.setData(clientsBytes);
        try {
            ProxyManager.sendMessage(name(), msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void syncSuccess() {
        if (status == ProxyStatus.syncStart) {
            this.status = ProxyStatus.running;
        }
    }

    @Override
    public boolean existSession(String client) {
        ReadLock readLock = lock.readLock();
        try {
            readLock.lock();
            return sessions.containsKey(client);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void putSession(String client, Session session) {
        if (existSession(client)) {
            return;
        }
        WriteLock writeLock = lock.writeLock();
        try {
            writeLock.lock();
            if (existSession(client)) {
                return;
            } else {
                sessions.put(client, session);
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Session removeSession(String client) {
        if (existSession(client)) {
            WriteLock writeLock = lock.writeLock();
            try {
                writeLock.lock();
                if (existSession(client)) {
                    return sessions.remove(client);
                }
            } finally {
                writeLock.unlock();
            }
        }
        return null;
    }

    @Override
    public Session getSession(String client) {
        ReadLock readLock = lock.readLock();
        try {
            readLock.lock();
            return sessions.get(client);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public String[] clients() {
        ReadLock readLock = lock.readLock();
        try {
            readLock.lock();
            return sessions.keySet().toArray(new String[0]);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void receive(SessionMessage... sms) {
        // 先按照消息类型进行分组
        Map<String, List<SessionMessage>> sessions = Arrays.stream(sms).collect(groupingBy(SessionMessage::getClient));
        // 遍历
        sessions.entrySet().stream().forEach(e -> {
            String client = e.getKey();
            List<SessionMessage> msgs = e.getValue();
            // 代理为客户端且不存在会话
            if (!isServerMode() && !existSession(client)) {
                putSession(client, new Session(client, this));
            }
            // 执行消息
            executor.execute(client, new Runnable() {
                @Override
                public void run() {
                    Session session = getSession(client);
                    if (session != null) {
                        session.receive(msgs.toArray(new SessionMessage[0]));
                    }
                }
            });
        });
    }

    protected void startTransporter() throws Exception {
        if (bus.start()) {
            log.info("传输引擎启动成功");
        } else {
            status = ProxyStatus.error;
            log.error("传输引擎启动失败");
            throw new RuntimeException("传输引擎启动失败");
        }
    }

    private Set<String> sessionKeys() {
        ReadLock readLock = lock.readLock();
        try {
            readLock.lock();
            return sessions.keySet();
        } finally {
            readLock.unlock();
        }
    }

}
