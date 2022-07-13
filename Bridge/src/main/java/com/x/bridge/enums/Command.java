package com.x.bridge.enums;

import com.x.bridge.bean.Message;
import com.x.bridge.netty.core.SocketConfig;
import com.x.bridge.netty.factory.SocketClient;
import com.x.bridge.proxy.session.ClientListener;
import com.x.bridge.proxy.session.Session;
import com.x.bridge.proxy.session.SessionManager;
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
    open(1) {
        @Override
        public void execute(Message msg, SessionManager manager) {
            // 客户端
            if (!manager.isServerMode()) {
                // 判断是否已经存在会话
                if (!manager.containSession(msg.getAppClient())) {
                    Session session = manager.createSession(msg.getAppClient());
                    manager.putSession(msg.getAppClient(), session);
                }
                Session session = manager.getSession(msg.getAppClient());
                // 未连接
                if (!session.isConnected()) {
                    // 禁止重复连接
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
    data(2) {
        @Override
        public void execute(Message msg, SessionManager manager) {
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
    close(3) {
        @Override
        public void execute(Message msg, SessionManager manager) {
            log.info("代理【{}】发来会话【{}】关闭命令", msg.getAgentServer(), msg.getAppClient());
            Session session = manager.getSession(msg.getAppClient());
            if (session != null) {
                session.close();
                session.setConnected(false);
            }
        }
    },
    timeout(4) {
        @Override
        public void execute(Message msg, SessionManager manager) {
            if (manager.isServerMode()) {
                log.info("代理【{}】会话【{}】建立超时", msg.getAgentServer(), msg.getAppClient());
                Session session = manager.getSession(msg.getAppClient());
                if (session != null) {
                    session.close();
                }
            }
        }
    },
    sync(5) {
        @Override
        public void execute(Message msg, SessionManager manager) {

        }
    },
    heartbeat(6) {
        @Override
        public void execute(Message msg, SessionManager manager) {

        }
    },
    openSuccess(801) {
        @Override
        public void execute(Message msg, SessionManager manager) {
            if (manager.isServerMode()) {
                log.info("代理【{}】会话【{}】建立成功", msg.getAgentServer(), msg.getAppClient());
                Session session = manager.getSession(msg.getAppClient());
                if (session != null) {
                    session.setConnected(true);
                }
            }
        }
    },
    openFail(802) {
        @Override
        public void execute(Message msg, SessionManager manager) {
            if (manager.isServerMode()) {
                log.info("代理【{}】会话【{}】建立失败", msg.getAgentServer(), msg.getAppClient());
                Session session = manager.getSession(msg.getAppClient());
                if (session != null) {
                    session.close();
                }
            }
        }
    };


    public final int code;

    private Command(int code) {
        this.code = code;
    }

    public static Command get(int code) {
        return commands.get(code);
    }

    private static final Map<Integer, Command> commands = new HashMap<>();

    static {
        for (Command cmd : values()) {
            if (commands.containsKey(cmd.code)) {
                throw new RuntimeException("命令【" + cmd.code + "】重复");
            }
            commands.put(cmd.code, cmd);
        }
    }

    public abstract void execute(Message msg, SessionManager manager);
}
