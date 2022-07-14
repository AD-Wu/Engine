package com.x.bridge.proxy.command.factory;

import com.x.bridge.bean.Message;
import com.x.bridge.proxy.command.core.Command;
import com.x.bridge.proxy.command.core.ICommand;

/**
 * @author AD
 * @date 2022/7/14 11:57
 */
public class Sync implements ICommand {

    @Override
    public void execute(Message msg) {

    }

    @Override
    public int getCode() {
        return Command.sync.code;
    }
}
