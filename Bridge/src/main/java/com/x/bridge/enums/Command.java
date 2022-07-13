package com.x.bridge.enums;

import com.x.bridge.bean.Message;
import com.x.bridge.netty.core.SocketConfig;
import com.x.bridge.netty.factory.SocketClient;
import com.x.bridge.proxy.interfaces.ISessionManager;
import com.x.bridge.proxy.session.ClientListener;
import com.x.bridge.proxy.session.Session;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;

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
            if (!manager.isServerMode()) {
                // 判断是否已经存在会话
                if (!manager.containSession(msg.getAppClient())) {
                    Session session = manager.createSession(msg.getAppClient());
                    manager.putSession(msg.getAppClient(), session);
                }
                Session session = manager.getSession(msg.getAppClient());
                // 禁止重复连接
                if (!session.isConnected()) {
                    synchronized (session.getLock()) {
                        if (!session.isConnected()) {
                            log.info("代理【{}】发来会话【{}】建立命令", msg.getAgentServer(), msg.getAppClient());
                            String appClient = msg.getAppClient();
                            SocketConfig conf = SocketConfig.getClientConfig(msg.getAppHost(), msg.getAppPort());
                            SocketClient client = new SocketClient(appClient, conf, new ClientListener(manager, appClient));
                            client.start();
                        }
                    }
                }
            }
        }
    },
    close {
        @Override
        public void execute(Message msg, ISessionManager manager) {
            log.info("代理【{}】发来会话【{}】关闭命令", msg.getAgentServer(), msg.getAppClient());
            Session session = manager.removeSession(msg.getAppClient());
            if (session != null) {
                session.close();
            }
        }
    },
    timeout {
        @Override
        public void execute(Message msg, ISessionManager manager) {
            if (manager.isServerMode()) {
                log.info("代理【{}】会话【{}】建立超时", msg.getAgentServer(), msg.getAppClient());
                Session session = manager.removeSession(msg.getAppClient());
                if (session != null) {
                    session.setConnected(false);
                }
            }
        }
    },
    openSuccess {
        @Override
        public void execute(Message msg, ISessionManager manager) {
            if (manager.isServerMode()) {
                log.info("代理【{}】会话【{}】建立成功", msg.getAgentServer(), msg.getAppClient());
                Session session = manager.getSession(msg.getAppClient());
                if (session != null) {
                    session.setConnected(true);
                }
            }
        }
    },
    openFail {
        @Override
        public void execute(Message msg, ISessionManager manager) {
            if (manager.isServerMode()) {
                log.info("代理【{}】会话【{}】建立失败", msg.getAgentServer(), msg.getAppClient());
                Session session = manager.removeSession(msg.getAppClient());
                if (session != null) {
                    session.setConnected(false);
                }
            }
        }
    },
    data {
        @Override
        public void execute(Message msg, ISessionManager manager) {
            if (manager.isServerMode()) {
                Session session = manager.getSession(msg.getAppClient());
                if (session != null) {
                    session.receive(msg);
                }
            } else {
                if (!manager.containSession(msg.getAppClient())) {
                    Session session = manager.createSession(msg.getAppClient());
                    manager.putSession(msg.getAppClient(), session);
                }
                Session session = manager.getSession(msg.getAppClient());
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
