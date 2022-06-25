package com.x.bridge.proxy.command.interfaces;

import com.x.bridge.bean.Message;
import com.x.bridge.proxy.core.Replier;

/**
 * 命令接口
 * @author AD
 * @date 2022/6/24 19:17
 */
public interface ICommand {

    void execute(Message msg, Replier replier);

}
