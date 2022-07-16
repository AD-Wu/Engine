package com.x.bridge.session;

import com.x.bridge.bean.Message;
import com.x.bridge.enums.MessageType;
import com.x.bridge.interfaces.Service;
import com.x.bridge.proxy.core.IProxy;
import com.x.doraemon.Arrayx;
import com.x.doraemon.therad.BalanceExecutor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;

/**
 * @author AD
 * @date 2022/7/14 15:01
 */
@Log4j2
public class SessionManager extends Service implements ISessionManager {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Map<String, Session> sessions = new HashMap<>();

    private final IProxy proxy;

    private BalanceExecutor<String> executor;

    public SessionManager(IProxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public String name() {
        return proxy.name();
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
    public Set<String> getSessionKeys() {
        ReadLock readLock = lock.readLock();
        try {
            readLock.lock();
            return sessions.keySet();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void sync(String[] validClients) {
        WriteLock writeLock = lock.writeLock();
        try {
            writeLock.lock();
            Set<String> invalids = sessions.keySet();
            if (Arrayx.isNotEmpty(validClients)) {
                Set<String> valids = Arrays.stream(validClients).collect(Collectors.toSet());
                invalids.removeAll(valids);
            }
            invalids.stream().forEach(client -> {
                Session session = sessions.remove(client);
                session.close();
            });
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    protected boolean onStart() throws Exception {
        executor = new BalanceExecutor<>(name());
        return true;
    }

    @Override
    protected void onStop() {
        if (executor != null) {
            executor.shutdown();
        }
    }

    @Override
    protected void onStartError(Throwable e) {
        stop();
    }

    @Override
    public void receive(Message... messages) {
        // 先按照消息类型进行分组
        Map<Integer, List<Message>> types = Arrays.stream(messages).collect(Collectors.groupingBy(Message::getType));
        types.entrySet().stream().forEach(e -> {
            int typeCode = e.getKey();
            List<Message> msgs = e.getValue();
            MessageType type = MessageType.get(typeCode);
            switch (type) {
                case socket:
                    handleSocketMessage(msgs);
                    break;
                case function:
                    handleFunctionMessage(msgs);
                    break;
                default:
                    break;

            }
        });

    }

    private void handleSocketMessage(List<Message> msgs) {
        Map<String, List<Message>> groups = msgs.stream().collect(Collectors.groupingBy(Message::getAppClient));
        groups.entrySet().stream().forEach(e -> {
            String client = e.getKey();
            List<Message> msg = e.getValue();
            // 代理为客户端且不存在会话
            if (!proxy.isServerMode() && !existSession(client)) {
                putSession(client, new Session(client, proxy));
            }
            executor.execute(client, new Runnable() {
                @Override
                public void run() {
                    Session session = getSession(client);
                    if (session != null) {
                        session.receive(msg.toArray(new Message[0]));
                    }
                }
            });
        });
    }

    private void handleFunctionMessage(List<Message> msgs) {
        Map<String, List<Message>> groups = msgs.stream().collect(Collectors.groupingBy(Message::getAppClient));
        groups.entrySet().stream().forEach(e -> {
            String client = e.getKey();
            List<Message> msg = e.getValue();
            executor.execute(client, new Runnable() {
                @Override
                public void run() {
                    Session session = new Session(client, proxy);
                    session.receive(msg.toArray(new Message[0]));
                }
            });
        });
    }

}
