package com.x.bridge.proxy.command.factory;

import com.x.bridge.bean.Message;
import com.x.bridge.netty.core.SocketConfig;
import com.x.bridge.netty.factory.SocketClient;
import com.x.bridge.proxy.command.core.Command;
import com.x.bridge.proxy.command.core.ICommand;
import com.x.bridge.proxy.session.ClientListener;
import com.x.bridge.proxy.session.SessionManager;
import lombok.extern.log4j.Log4j2;

/**
 * @author AD
 * @date 2022/7/14 10:27
 */
@Log4j2
public class Open implements ICommand {

    private final SessionManager manager;
    private final ClientListener listener;

    public Open(SessionManager manager, ClientListener listener) {
        this.manager = manager;
        this.listener = listener;
    }

    @Override
    public void execute(Message msg) {
        log.info("代理【{}】发来会话【{}】建立命令", msg.getAgentServer(), msg.getAppClient());
        String appClient = msg.getAppClient();
        SocketConfig conf = SocketConfig.getClientConfig(msg.getAppHost(), msg.getAppPort());
        SocketClient client = new SocketClient(appClient, conf, listener);
        client.start();
    }

    @Override
    public int getCode() {
        return Command.open.code;
    }
}
