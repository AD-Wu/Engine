package com.x.bridge.proxy.factory;

import com.x.bridge.proxy.interfaces.ICommand;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 命令管理者
 * @author AD
 * @date 2022/6/25 11:49
 */
public final class CommandManager {

    public static final Map<String, ICommand> COMMANDS = new ConcurrentHashMap<>();

    public static ICommand getCommand(String cmd){
        return COMMANDS.get(cmd);
    }
}
