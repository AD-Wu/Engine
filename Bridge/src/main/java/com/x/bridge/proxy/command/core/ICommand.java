package com.x.bridge.proxy.command.core;

import com.x.bridge.proxy.core.Message;

/**
 * 命令接口
 * @author AD
 * @date 2022/6/24 19:17
 */
public interface ICommand {
    
    void execute(Message msg);
    
}
