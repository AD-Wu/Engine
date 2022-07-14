package com.x.bridge.session;

import com.x.bridge.bean.Message;
import com.x.doraemon.therad.BalanceExecutor;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;

/**
 * @author AD
 * @date 2022/7/14 15:01
 */
@Log4j2
public class SessionManager implements ISessionManager {

    private Map<String, Session> sessions = new ConcurrentHashMap<>();

    private String name;

    private BalanceExecutor<String> executor;

    public SessionManager(String name) {
        this.name = name;

    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean isAccept(String client) {
        return false;
    }

    @Override
    public boolean existSession(String client) {
        return sessions.containsKey(client);
    }

    @Override
    public void putSession(String client, Session session) {
        if (!sessions.containsKey(client)) {
            sessions.put(client, session);
        }
    }

    @Override
    public Session removeSession(String client) {
        return sessions.remove(client);
    }

    @Override
    public Session getSession(String client) {
        return sessions.get(client);
    }

    @Override
    public boolean start() {
        executor = new BalanceExecutor<>(name);
        return true;
    }

    @Override
    public void stop() {
        executor.shutdown();
    }

    @Override
    public void receive(Message... messages) {
        Map<String, List<Message>> groups = Arrays.stream(messages).collect(Collectors.groupingBy(Message::getAppClient));
        groups.entrySet().stream().forEach(e -> {
            String client = e.getKey();
            List<Message> msg = e.getValue();
            Session session = getSession(client);
            if (session != null) {
                executor.execute(client, new Runnable() {
                    @Override
                    public void run() {
                        session.receive(msg.toArray(new Message[0]));
                    }
                });
            }
        });
    }

}
