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
    
    public ProxyClient(ProxyConfig conf) {
        super(conf);
    }
    
    @Override
    protected boolean onStart() throws Exception {
        startTransporter();
        startSessionManager();
        status(ProxyStatus.syncStart);
        sync(true);
        return true;
    }
    
    @Override
    protected void onStop() {
        status(ProxyStatus.stopped);
        transporter.stop();
        sessions.stop();
    }
    
    @Override
    public boolean isServerMode() {
        return false;
    }
    
}
