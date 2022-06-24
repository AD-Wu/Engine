package com.x.bridge.proxy.command.factory;

import com.x.bridge.proxy.command.core.ICommand;
import com.x.bridge.proxy.core.Message;
import com.x.bridge.proxy.core.ProxyContext;
import com.x.bridge.proxy.factory.ProxyClient;

/**
 * 建立连接命令
 * @author AD
 * @date 2022/6/25 00:05
 */
public class Open implements ICommand {
    
    @Override
    public void execute(Message msg) {
        ProxyContext ctx = new ProxyContext();
        ctx.setAppClient(msg.getAppClient());
        ctx.setAppHost(msg.getAppHost());
        ctx.setAppPort(msg.getAppPort());
        ctx.setProxyServer(msg.getProxyServer());
        ProxyClient client = new ProxyClient(ctx);
        client.start();
    }
    
}
