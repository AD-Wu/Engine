package com.x.bridge.proxy.core;

import com.x.bridge.bean.Msg;
import com.x.bridge.proxy.ProxyManager;
import com.x.bridge.proxy.cmd.Cmd;
import com.x.bridge.bean.SessionMsg;
import com.x.bridge.proxy.enums.MsgType;
import com.x.bridge.proxy.enums.ProxyStatus;
import com.x.bridge.proxy.enums.TransportMode;
import com.x.bridge.transport.core.IReader;
import com.x.bridge.transport.core.ITransporter;
import com.x.bridge.transport.core.IWriter;
import com.x.bridge.transport.core.Transporter;
import com.x.doraemon.Strings;
import com.x.doraemon.therad.BalanceExecutor;
import lombok.extern.log4j.Log4j2;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import static java.util.stream.Collectors.groupingBy;

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
    
    protected ITransporter transporter;
    
    public ProxyService(ProxyConfig conf) {
        this.conf = conf;
        IReader reader = TransportMode.get(conf.getReadMode()).createReader(this);
        IWriter writer = TransportMode.get(conf.getWriteMode()).createWriter(this);
        this.transporter = new Transporter(this, reader, writer);
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
    public ITransporter getTransporter() {
        return transporter;
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
        Msg msg = new Msg();
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
    public void receive(SessionMsg... sms) {
        // 先按照消息类型进行分组
        Map<String, List<SessionMsg>> sessions = Arrays.stream(sms).collect(groupingBy(SessionMsg::getClient));
        // 遍历
        sessions.entrySet().stream().forEach(e -> {
            String client = e.getKey();
            List<SessionMsg> msgs = e.getValue();
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
                        session.receive(msgs.toArray(new SessionMsg[0]));
                    }
                }
            });
        });
    }
    
    protected void startTransporter() throws Exception {
        if (transporter.start()) {
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
