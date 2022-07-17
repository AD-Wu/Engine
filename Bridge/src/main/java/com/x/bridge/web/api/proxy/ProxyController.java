package com.x.bridge.web.api.proxy;

import com.x.bridge.proxy.ProxyManager;
import com.x.bridge.proxy.core.ProxyConfig;
import com.x.bridge.web.api.proxy.data.ProxyReq;
import com.x.bridge.web.api.proxy.data.ProxyRes;
import com.x.doraemon.common.web.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 代理API
 * @author AD
 * @date 2022/7/16 23:53
 */
@RequestMapping("proxy")
@RestController
public class ProxyController {
    
    @PostMapping("create")
    public Result<ProxyRes> create(@RequestBody ProxyReq req) {
        ProxyConfig conf = new ProxyConfig();
        BeanUtils.copyProperties(req, conf);
        return null;
    }
    
    public boolean start(String name) {
        return ProxyManager.startProxy(name);
    }
    
    public void stop(String name) {
        ProxyManager.stopProxy(name);
    }
    
}
