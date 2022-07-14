package com.x.bridge.proxy.factory;

import com.x.bridge.bean.Message;
import com.x.bridge.enums.ProxyStatus;
import com.x.bridge.enums.TransportEngineStatus;
import com.x.bridge.proxy.interfaces.IProxy;
import com.x.bridge.proxy.session.Session;
import com.x.bridge.proxy.session.SessionManager;
import com.x.bridge.transport.interfaces.ITransportEngine;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;

/**
 * @author AD
 * @date 2022/7/11 19:49
 */
@Log4j2
public class Proxy implements IProxy {

    /**
     * 传输引擎
     */
    private final ITransportEngine transportEngine;

    /**
     * socket会话管理者
     */
    private final SessionManager sessionManager;


    public Proxy(ITransportEngine transportEngine, SessionManager sessionManager) {
        this.transportEngine = transportEngine;
        this.sessionManager = sessionManager;
    }

    @Override
    public ProxyStatus status() {
        return null;
    }

    @Override
    public TransportEngineStatus transportStatus() {
        return transportEngine.status();
    }

    @Override
    public String name() {
        return sessionManager.name();
    }

    @Override
    public boolean isServerMode() {
        return sessionManager.isServerMode();
    }

    @Override
    public boolean start() {
        if (transportEngine.start()) {
            log.info("传输引擎启动成功");
            if (sessionManager.start()) {
                log.info("Socket会话管理启动成功");
                return true;
            }
            log.info("Socket会话管理启动成功");
            return false;
        }
        log.error("传输引擎启动失败");
        return false;
    }

    @Override
    public void stop() {
        sessionManager.stop();
        transportEngine.stop();
        log.info("代理关闭:【{}】", name());
    }

    @Override
    public void receive(Message... messages) {
        // 分组
        Map<String, List<Message>> groups = Arrays.asList(messages).stream().parallel()
            .collect(Collectors.groupingBy(Message::getAppClient));

        groups.entrySet().stream().forEach(entry -> {
            String client = entry.getKey();
            Message[] msgs = entry.getValue().toArray(new Message[0]);
            // 客户端
            if (!isServerMode()) {
                if (!sessionManager.containSession(client)) {
                    Session session = sessionManager.createSession(client);
                    sessionManager.putSession(client, session);
                }
            }
            Session session = sessionManager.getSession(client);
            if (session != null) {
                session.receive(msgs);
            }
        });
    }
}
