package com.x.bridge.proxy.cmd;

import com.x.bridge.netty.core.SocketConfig;
import com.x.bridge.netty.factory.SocketClient;
import com.x.bridge.proxy.core.IProxyService;
import com.x.bridge.bean.SessionMsg;
import com.x.bridge.proxy.core.Session;
import com.x.bridge.proxy.socket.client.SocketClientListener;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;

/**
 * 命令
 * @author AD
 * @date 2022/6/21 21:43
 */
@Log4j2
public enum SessionCmd implements ISessionCmd {
    open(10) {
        @Override
        public void execute(SessionMsg msg, Session session, IProxyService proxy) {
            log.info("代理【{}】发来会话【{}】建立命令", msg.getProxyName(), msg.getClient());
            String appClient = msg.getClient();
            SocketConfig conf = SocketConfig.getClientConfig(msg.getAppHost(), msg.getAppPort());
            SocketClient client = new SocketClient(conf, new SocketClientListener(appClient, proxy));
            client.start();
        }
    },
    data(11) {
        @Override
        public void execute(SessionMsg msg, Session session, IProxyService proxy) {
            session.sendToApp(msg.getData());
        }
    },
    close(12) {
        @Override
        public void execute(SessionMsg msg, Session session, IProxyService proxy) {
            session.close();
        }
    },
    timeout(13) {
        @Override
        public void execute(SessionMsg msg, Session session, IProxyService proxy) {
            session.close();
        }
    },
   
    heartbeat(6) {
        @Override
        public void execute(SessionMsg msg, Session session, IProxyService proxy) {
        
        }
    },
    openSuccess(80100) {
        @Override
        public void execute(SessionMsg msg, Session session, IProxyService proxy) {
            session.setConnected(true);
        }
    },
    openFail(80101) {
        @Override
        public void execute(SessionMsg msg, Session session, IProxyService proxy) {
            session.setConnected(false);
        }
    };
    
    
    @Override
    public int code() {
        return this.code;
    }
    
    public final int code;
    
    private SessionCmd(int code) {
        this.code = code;
    }
    
    public static ISessionCmd get(int code) {
        return commands.get(code);
    }
    
    public static boolean register(int code, ISessionCmd cmd) {
        if (commands.containsKey(code)) {
            throw new RuntimeException("命令【" + code + "】重复");
        }
        commands.put(code, cmd);
        return true;
    }
    
    private static final Map<Integer, ISessionCmd> commands = new HashMap<>();
    
    static {
        for (SessionCmd cmd : values()) {
            if (commands.containsKey(cmd.code)) {
                throw new RuntimeException("命令【" + cmd.code + "】重复");
            }
            commands.put(cmd.code, cmd);
        }
    }
    
}
