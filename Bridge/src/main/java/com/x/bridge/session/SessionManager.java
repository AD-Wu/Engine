package com.x.bridge.session;

import com.x.bridge.bean.Message;
import com.x.bridge.enums.MessageType;
import com.x.bridge.interfaces.Service;
import com.x.bridge.proxy.core.IProxy;
import com.x.doraemon.therad.BalanceExecutor;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public void sync() {
        String clients = sessions.keySet().stream().collect(Collectors.joining(","));
        byte[] bytes = clients.getBytes(StandardCharsets.UTF_8);
        Message msg = new Message();
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
            MessageType type = MessageType.get(typeCode);
            switch (type) {
                case socket:
                    handleSocketMessage(e.getValue());
                    break;
                case function:
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
        // 同一类型的消息
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

}
