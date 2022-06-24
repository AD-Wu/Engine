package com.x.bridge.proxy.command.factory;

import com.x.bridge.proxy.ProxyManager;
import com.x.bridge.proxy.command.core.ICommand;
import com.x.bridge.proxy.core.Message;

/**
 * 超时命令
 * @author AD
 * @date 2022/6/25 00:48
 */
public class Timeout implements ICommand {
    
    @Override
    public void execute(Message msg) {
        ProxyManager.closeProxyClient(msg.getProxyServer(), msg.getAppClient());
    }
    
}
