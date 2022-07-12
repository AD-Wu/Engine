package com.x.bridge.enums;

import com.x.bridge.bean.Message;
import com.x.bridge.netty.core.SocketConfig;
import com.x.bridge.netty.factory.SocketClient;
import com.x.bridge.proxy.interfaces.ISessionManager;
import com.x.bridge.proxy.session.Session;
import com.x.bridge.proxy.session.ClientListener;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.log4j.Log4j2;

/**
 * 命令
 * @author AD
 * @date 2022/6/21 21:43
 */
@Log4j2
public enum Command {
    open {
        @Override
        public void execute(Message msg, ISessionManager manager) {
            log.info("另一端代理【{}】发来会话【{}】建立命令", msg.getAgentServer(), msg.getAppClient());
            String appClient = msg.getAppClient();
            SocketConfig conf = SocketConfig.getClientConfig(msg.getAppHost(), msg.getAppPort());
            SocketClient client = new SocketClient(appClient, conf, new ClientListener(manager, msg.getAppClient()));
            client.start();
        }
    },
    close {
        @Override
        public void execute(Message msg, ISessionManager manager) {
            log.info("另一端代理【{}】发来会话【{}】关闭命令", msg.getAgentServer(), msg.getAppClient());
            manager.closeSession(msg.getAppClient());
        }
    },
    timeout {
        @Override
        public void execute(Message msg, ISessionManager manager) {
            log.info("另一端代理【{}】会话【{}】建立超时", msg.getAgentServer(), msg.getAppClient());
            Session session = manager.closeSession(msg.getAppClient());
            if (session != null) {
                session.setConnectSuccess(false);
            }
        }
    },
    openSuccess {
        @Override
        public void execute(Message msg, ISessionManager manager) {
            log.info("另一端代理【{}】会话【{}】建立成功", msg.getAgentServer(), msg.getAppClient());
            Session session = manager.getSession(msg.getAppClient());
            if (session != null) {
                session.setConnectSuccess(true);
            }
        }
    },
    openFail {
        @Override
        public void execute(Message msg, ISessionManager manager) {
            log.info("另一端代理【{}】会话【{}】建立失败", msg.getAgentServer(), msg.getAppClient());
            manager.closeSession(msg.getAppClient());
        }
    },
    data {
        @Override
        public void execute(Message msg, ISessionManager manager) {
            Session session = manager.getSession(msg.getAppClient());
            if (session != null) {
                session.receive(msg);
            }
        }
    },
    sync {
        @Override
        public void execute(Message msg, ISessionManager manager) {

        }
    },
    heartbeat {
        @Override
        public void execute(Message msg, ISessionManager manager) {

        }
    };

    public static Command get(String key) {
        return commands.get(key);
    }

    private static final Map<String, Command> commands = new HashMap<>();

    static {
        for (Command cmd : values()) {
            commands.put(cmd.toString(), cmd);
        }
    }

    public abstract void execute(Message msg, ISessionManager manager);
}
