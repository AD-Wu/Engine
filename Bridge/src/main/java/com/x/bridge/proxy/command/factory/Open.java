package com.x.bridge.proxy.command.factory;

import com.x.bridge.proxy.command.interfaces.ICommand;
import com.x.bridge.bean.Message;
import com.x.bridge.proxy.core.ProxyContext;
import com.x.bridge.proxy.core.Replier;
import com.x.bridge.proxy.factory.ProxyClient;

/**
 * 建立连接命令
 * @author AD
 * @date 2022/6/25 00:05
 */
public class Open implements ICommand {

    @Override
    public void execute(Message msg, Replier replier) {
        ProxyContext ctx = new ProxyContext();
        ctx.setAppHost(msg.getAppHost());
        ctx.setAppPort(msg.getAppPort());
        ctx.setProxyServer(msg.getProxyServer());
        ProxyClient client = new ProxyClient(ctx, msg.getAppClient());
        client.start();
    }

}
