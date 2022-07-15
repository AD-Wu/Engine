package com.x.bridge.proxy.server;

import com.x.bridge.enums.ProxyStatus;
import com.x.bridge.netty.factory.SocketServer;
import com.x.bridge.netty.interfaces.ISocket;
import com.x.bridge.proxy.core.Proxy;
import java.util.Set;
import lombok.extern.log4j.Log4j2;

/**
 * @author AD
 * @date 2022/7/14 16:38
 */
@Log4j2
public class ProxyServer extends Proxy {

    private ProxyServerConfig conf;
    private ISocket server;

    public ProxyServer(String name, ProxyServerConfig conf) {
        super(name, conf);
        this.server = new SocketServer(conf.getPort(), new SocketServerListener(this));
    }

    @Override
    public boolean isAccept(String client) {
        Set<String> allows = conf.getAllowClients();
        return (allows == null || allows.size() == 0) ? true : allows.contains(client);
    }

    @Override
    public ProxyStatus status() {
        return null;
    }

    @Override
    public boolean start() {
        if (transporter.start()) {
            log.info("传输引擎启动成功");
            if (server.start()) {
                log.info("socket服务器启动成功,端口【{}】", conf.getPort());
                if (sessions.start()) {
                    log.info("会话管理启动成功");
                    status = ProxyStatus.running;
                    return true;
                } else {
                    log.info("会话管理启动失败");
                    status = ProxyStatus.sessionError;
                }
            } else {
                log.info("socket服务器启动失败");
                status = ProxyStatus.socketServerError;
            }
        } else {
            log.error("传输引擎启动失败");
            status = ProxyStatus.transportError;
        }
        return false;
    }

    @Override
    public void stop() {
        transporter.stop();
        server.stop();
        sessions.stop();
    }
}
