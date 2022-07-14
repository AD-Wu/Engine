package com.x.bridge.proxy.command.factory;

import com.x.bridge.bean.Message;
import com.x.bridge.proxy.command.core.Command;
import com.x.bridge.proxy.command.core.ICommand;
import com.x.bridge.proxy.session.Session;

/**
 * @author AD
 * @date 2022/7/14 11:46
 */
public class Timeout implements ICommand {

    private final Session session;

    public Timeout(Session session) {
        this.session = session;
    }

    @Override
    public void execute(Message msg) {
        session.close();
    }

    @Override
    public int getCode() {
        return Command.timeout.code;
    }
}
