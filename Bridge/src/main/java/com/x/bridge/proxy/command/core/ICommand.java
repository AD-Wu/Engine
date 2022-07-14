package com.x.bridge.proxy.command.core;

import com.x.bridge.bean.Message;

/**
 * @author AD
 * @date 2022/7/14 10:26
 */
public interface ICommand {

    void execute(Message msg);

    int getCode();
}
