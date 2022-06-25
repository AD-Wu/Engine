package com.x.bridge.proxy.command.factory;

import com.x.bridge.proxy.ProxyManager;
import com.x.bridge.proxy.command.interfaces.ICommand;
import com.x.bridge.bean.Message;
import com.x.bridge.proxy.core.Replier;

/**
 * 连接关闭命令
 * @author AD
 * @date 2022/6/25 00:11
 */
public class Close implements ICommand {

    @Override
    public void execute(Message msg, Replier replier) {
        ProxyManager.closeReplier(msg.getProxyServer(), msg.getAppClient());
    }

}
