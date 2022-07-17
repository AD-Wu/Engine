package com.x.bridge.proxy.socket.client;

import com.x.bridge.proxy.enums.ProxyStatus;
import com.x.bridge.proxy.core.ProxyService;
import com.x.bridge.proxy.core.ProxyConfig;
import lombok.extern.log4j.Log4j2;

/**
 * @author AD
 * @date 2022/7/14 16:59
 */
@Log4j2
public class ProxyClient extends ProxyService {
    
    public ProxyClient(ProxyConfig conf) {
        super(conf);
    }
    
    @Override
    protected boolean onStart() throws Exception {
        startTransporter();
        sync(true);
        return true;
    }
    
    @Override
    protected void onStop() {
        status = ProxyStatus.stopped;
        transporter.stop();
    }
    
    @Override
    public boolean isServerMode() {
        return false;
    }
    
}
