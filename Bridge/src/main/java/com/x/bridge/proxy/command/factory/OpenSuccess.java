package com.x.bridge.proxy.command.factory;

import com.x.bridge.proxy.command.interfaces.ICommand;
import com.x.bridge.bean.Message;
import com.x.bridge.proxy.core.Replier;

/**
 * 连接建立成功响应命令
 * @author AD
 * @date 2022/6/25 00:49
 */
public class OpenSuccess implements ICommand {

    @Override
    public void execute(Message msg, Replier replier) {

    }

}
