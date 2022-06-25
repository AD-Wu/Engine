package com.x.bridge.proxy.command.factory;

import com.x.bridge.bean.Message;
import com.x.bridge.proxy.command.interfaces.ICommand;
import com.x.bridge.proxy.core.Replier;

/**
 * 超时命令
 * @author AD
 * @date 2022/6/25 00:48
 */
public class Timeout implements ICommand {

    @Override
    public void execute(Message msg, Replier replier) {
        replier.setOpenConnect(false);
    }

}
