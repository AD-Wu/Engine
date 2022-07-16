package com.x.bridge.enums;

import com.x.bridge.bean.Message;
import com.x.bridge.netty.core.SocketConfig;
import com.x.bridge.netty.factory.SocketClient;
import com.x.bridge.proxy.client.SocketClientListener;
import com.x.bridge.proxy.core.ICommand;
import com.x.bridge.proxy.core.IProxy;
import com.x.bridge.session.Session;
import lombok.extern.log4j.Log4j2;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 命令
 * @author AD
 * @date 2022/6/21 21:43
 */
@Log4j2
public enum Command implements ICommand {
    open(1) {
        @Override
        public void execute(Message msg, Session session, IProxy proxy) {
            log.info("代理【{}】发来会话【{}】建立命令", msg.getProxyName(), msg.getAppClient());
            String appClient = msg.getAppClient();
            SocketConfig conf = SocketConfig.getClientConfig(msg.getAppHost(), msg.getAppPort());
            SocketClient client = new SocketClient(conf, new SocketClientListener(appClient, proxy));
            client.start();
        }
    },
    data(2) {
        @Override
        public void execute(Message msg, Session session, IProxy proxy) {
            session.sendToApp(msg.getData());
        }
    },
    close(3) {
        @Override
        public void execute(Message msg, Session session, IProxy proxy) {
            session.close();
        }
    },
    timeout(4) {
        @Override
        public void execute(Message msg, Session session, IProxy proxy) {
            session.close();
        }
    },
    sync(5) {
        @Override
        public void execute(Message msg, Session session, IProxy proxy) {
            byte[] data = msg.getData();
            String[] clients = null;
            if (data != null && data.length > 0) {
                clients = new String(data, StandardCharsets.UTF_8).split(",");
            } else {
                // 没有socket连接,清空所有传输媒介的数据
                proxy.getTransporter().clear();
            }
            // 同步会话缓存
            proxy.getSessionManager().sync(clients);
            // 发送响应命令
            Message resp = Message.newCommand(proxy.name(), syncSuccess.code, msg.getAppClient());
            resp.setAppHost(msg.getAppHost());
            resp.setAppPort(msg.getAppPort());
            try {
                proxy.getTransporter().write(resp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    },
    heartbeat(6) {
        @Override
        public void execute(Message msg, Session session, IProxy proxy) {
        
        }
    },
    openSuccess(801) {
        @Override
        public void execute(Message msg, Session session, IProxy proxy) {
            session.setConnected(true);
        }
    },
    openFail(802) {
        @Override
        public void execute(Message msg, Session session, IProxy proxy) {
            session.setConnected(false);
        }
    },
    syncSuccess(805) {
        @Override
        public void execute(Message msg, Session session, IProxy proxy) {
            if (proxy.status() == ProxyStatus.syncStart) {
                proxy.status(ProxyStatus.running);
                log.info("代理【{}】同步启动成功", proxy.name());
            } else {
                log.info("代理【{}】同步会话成功", proxy.name());
            }
        }
    };
    
    @Override
    public int code() {
        return this.code;
    }
    
    public final int code;
    
    private Command(int code) {
        this.code = code;
    }
    
    public static ICommand get(int code) {
        return commands.get(code);
    }
    
    public static boolean register(int code, ICommand cmd) {
        if (commands.containsKey(code)) {
            throw new RuntimeException("命令【" + code + "】重复");
        }
        commands.put(code, cmd);
        return true;
    }
    
    private static final Map<Integer, ICommand> commands = new HashMap<>();
    
    static {
        for (Command cmd : values()) {
            if (commands.containsKey(cmd.code)) {
                throw new RuntimeException("命令【" + cmd.code + "】重复");
            }
            commands.put(cmd.code, cmd);
        }
    }
    
}
