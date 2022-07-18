package com.x.bridge.proxy.core;

import com.x.bridge.proxy.enums.ProxyStatus;
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
        bus.stop();
    }

    @Override
    public boolean isServerMode() {
        return false;
    }

}
