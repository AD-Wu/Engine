package com.x.bridge.proxy.command;

import com.x.bridge.bean.Message;
import com.x.bridge.netty.core.SocketConfig;
import com.x.bridge.netty.factory.SocketClient;
import com.x.bridge.proxy.interfaces.IProxy;
import com.x.bridge.session.Session;
import com.x.bridge.socket.listener.ClientListener;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.log4j.Log4j2;

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
            log.info("代理【{}】发来会话【{}】建立命令", msg.getAgentServer(), msg.getAppClient());
            String appClient = msg.getAppClient();
            SocketConfig conf = SocketConfig.getClientConfig(msg.getAppHost(), msg.getAppPort());
            SocketClient client = new SocketClient(conf, new ClientListener(appClient, proxy));
            client.start();
        }
    },
    data(2) {
        @Override
        public void execute(Message msg, Session session, IProxy proxy) {
            session.sendToTarget(msg.getData());
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
