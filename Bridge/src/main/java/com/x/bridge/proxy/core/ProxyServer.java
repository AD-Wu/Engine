package com.x.bridge.proxy.core;

import com.x.bridge.proxy.core.socket.SocketServerListener;
import com.x.bridge.proxy.enums.ProxyStatus;
import com.x.bridge.netty.factory.SocketServer;
import com.x.bridge.netty.interfaces.ISocket;
import lombok.extern.log4j.Log4j2;

import java.util.Set;

/**
 * @author AD
 * @date 2022/7/14 16:38
 */
@Log4j2
public class ProxyServer extends ProxyService {

    private ISocket server;

    public ProxyServer(ProxyConfig conf) {
        super(conf);
        this.server = new SocketServer(conf.getPort(), new SocketServerListener(this));
    }

    @Override
    public boolean isServerMode() {
        return true;
    }

    @Override
    public boolean isAccept(String client) {
        Set<String> allows = conf.getAllowClients();
        return (allows == null || allows.size() == 0) ? true : allows.contains(client);
    }

    @Override
    protected boolean onStart() throws Exception {
        if (status == ProxyStatus.created) {
            startTransporter();
            sync(true);
            startSocketServer();
            return true;
        }
        throw new RuntimeException("无法启动，代理当前状态【" + status + "】");
    }

    @Override
    protected void onStop() {
        status = ProxyStatus.stopped;
        bus.stop();
        server.stop();
    }

    private void startSocketServer() throws Exception {
        if (server.start()) {
            log.info("socket服务器启动成功,端口【{}】", conf.getPort());
        } else {
            status = ProxyStatus.error;
            log.info("socket服务器启动失败");
            throw new RuntimeException("socket服务器启动失败");
        }
    }

}
