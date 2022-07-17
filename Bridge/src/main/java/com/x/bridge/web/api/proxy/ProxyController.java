package com.x.bridge.web.api.proxy;

import com.x.bridge.proxy.ProxyManager;
import com.x.bridge.web.api.proxy.data.ProxyServerReq;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 代理控制器
 * @author AD
 * @date 2022/7/16 23:53
 */
@RequestMapping("proxy")
@RestController
public class ProxyController {
    
    @PostMapping("create/server")
    public void createServer(ProxyServerReq req) {
        ProxyManager.createProxy(null, true);
    }
    
    public void createClient(ProxyServerReq req) {
        ProxyManager.createProxy(null, false);
    }
    
    public boolean start(String name) {
        return ProxyManager.startProxy(name);
    }
    
    public void stop(String name) {
        ProxyManager.stopProxy(name);
    }
    
}
