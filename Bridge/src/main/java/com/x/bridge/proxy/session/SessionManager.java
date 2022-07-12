package com.x.bridge.proxy.session;

import com.x.bridge.netty.factory.SocketServer;
import com.x.bridge.netty.interfaces.ISocket;
import com.x.bridge.proxy.data.AgentConfig;
import com.x.bridge.proxy.interfaces.ISessionManager;
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
public class SessionManager implements ISessionManager {

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

    @Override
    public AgentConfig getAgentConfig() {
        return conf;
    }

    @Override
    public boolean isAccept(String clientHost) {
        Set<String> allows = conf.getAllowClients();
        if (allows == null || allows.size() == 0) {
            return true;
        } else {
            return conf.getAllowClients().contains(clientHost);
        }
    }

    @Override
    public synchronized void addSession(String appClient, Session session) {
        if (!sessions.containsKey(appClient)) {
            sessions.put(appClient, session);
            log.info("新增会话:【{}】", appClient);
        }
    }

    @Override
    public synchronized Session closeSession(String appClient) {
        Session session = sessions.remove(appClient);
        if (session != null) {
            session.close();
            log.info("关闭会话:【{}】", appClient);
        }
        return session;
    }

    @Override
    public synchronized Session getSession(String appClient) {
        return sessions.get(appClient);
    }

    @Override
    public Session createSession(String appClient) {
        return new Session(appClient, this);
    }

    @Override
    public Session createSession(ChannelHandlerContext ctx, String appClient) {
        return new Session(ctx, appClient, this);
    }


}
