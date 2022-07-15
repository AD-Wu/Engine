package com.x.bridge.proxy.client;

import com.x.bridge.enums.ProxyStatus;
import com.x.bridge.proxy.core.Proxy;
import com.x.bridge.proxy.core.ProxyConfig;
import lombok.extern.log4j.Log4j2;

/**
 * @author AD
 * @date 2022/7/14 16:59
 */
@Log4j2
public class ProxyClient extends Proxy {

    public ProxyClient(String name, ProxyConfig conf) {
        super(name, conf);
    }


    @Override
    public boolean start() {
        if (transporter.start()) {
            log.info("传输引擎启动成功");
            if (sessions.start()) {
                log.info("会话管理启动成功");
                status = ProxyStatus.running;
                return true;
            } else {
                log.info("会话管理启动失败");
            }
        } else {
            log.error("传输引擎启动失败");
        }
        status = ProxyStatus.error;
        return false;
    }

    @Override
    public void stop() {
        transporter.stop();
        sessions.stop();
    }
}
