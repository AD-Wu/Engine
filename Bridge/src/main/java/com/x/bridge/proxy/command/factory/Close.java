package com.x.bridge.proxy.command.factory;

import com.x.bridge.bean.Message;
import com.x.bridge.proxy.command.core.Command;
import com.x.bridge.proxy.command.core.ICommand;
import com.x.bridge.proxy.session.Session;

/**
 * @author AD
 * @date 2022/7/14 11:44
 */
public class Close implements ICommand {

    private final Session session;

    public Close(Session session) {
        this.session = session;
    }

    @Override
    public void execute(Message msg) {
        session.close();
    }

    @Override
    public int getCode() {
        return Command.close.code;
    }
}
