package com.x.bridge.proxy.interfaces;

import com.x.bridge.enums.ProxyStatus;
import com.x.bridge.session.ISessionManager;
import com.x.bridge.transport.interfaces.ITransportEngine;
import lombok.extern.log4j.Log4j2;

/**
 * @author AD
 * @date 2022/7/14 16:59
 */
@Log4j2
public class ProxyClient extends Proxy {

    private String name;
    private ProxyConfig conf;
    private ITransportEngine transporter;
    private ISessionManager sessions;

    public ProxyClient(String name, ProxyConfig conf) {
       super(name,conf);
    }

    @Override
    public ProxyStatus status() {
        return null;
    }

    @Override
    public boolean start() {
        if (transporter.start()) {
            log.info("传输引擎启动成功");
            if (sessions.start()) {
                log.info("会话管理启动成功");
                return true;
            } else {
                log.info("会话管理启动失败");
            }
        } else {
            log.error("传输引擎启动失败");
        }
        return false;
    }

    @Override
    public void stop() {
        transporter.stop();
        sessions.stop();
    }
}
