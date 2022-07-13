package com.x.bridge.proxy.session;

import com.x.bridge.netty.factory.SocketServer;
import com.x.bridge.netty.interfaces.ISocket;
import com.x.bridge.proxy.data.AgentConfig;
import io.netty.channel.ChannelHandlerContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.extern.log4j.Log4j2;

/**
 * @author AD
 * @date 2022/7/12 12:15
 */
@Log4j2
public class SessionManager implements ISocket {

    private final AgentConfig conf;
    private final Map<String, Session> sessions;
    private ISocket server;

    public SessionManager(AgentConfig conf) {
        this.conf = conf;
        this.sessions = new HashMap<>();
        if (isServerMode()) {
            this.server = new SocketServer(conf.getName(), conf.getAgentPort(), new ServerListener(this));
        }
    }

    @Override
    public String name() {
        return conf.getName();
    }

    @Override
    public boolean isServerMode() {
        return conf.getAgentPort() > 0;
    }

    @Override
    public boolean start() {
        if (isServerMode()) {
            boolean start = server.start();
            return start;
        }
        return true;
    }

    @Override
    public void stop() {
        if (isServerMode()) {
            server.stop();
        }
    }

    public AgentConfig getAgentConfig() {
        return conf;
    }

    public boolean isAccept(String clientHost) {
        Set<String> allows = conf.getAllowClients();
        if (allows == null || allows.size() == 0) {
            return true;
        } else {
            return conf.getAllowClients().contains(clientHost);
        }
    }

    public synchronized boolean containSession(String appClient) {
        return sessions.containsKey(appClient);
    }

    public synchronized void putSession(String appClient, Session session) {
        if (!sessions.containsKey(appClient)) {
            sessions.put(appClient, session);
            log.info("新增会话:【{}】", appClient);
        }
    }

    synchronized Session removeSession(String appClient) {
        Session session = sessions.remove(appClient);
        if (session != null) {
            log.info("移除会话:【{}】", appClient);
        }
        return session;
    }

    public synchronized Session getSession(String appClient) {
        return sessions.get(appClient);
    }

    public Session createSession(String appClient) {
        return new Session(appClient, this);
    }

    public Session createSession(ChannelHandlerContext ctx, String appClient) {
        return new Session(ctx, appClient, this);
    }


}
