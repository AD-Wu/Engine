package com.x.bridge.proxy.command.core;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.log4j.Log4j2;

/**
 * 命令
 * @author AD
 * @date 2022/6/21 21:43
 */
@Log4j2
public enum Command {
    open(1),
    data(2),
    close(3),
    timeout(4),
    sync(5),
    heartbeat(6),
    openSuccess(801),
    openFail(802);

    public final int code;

    private Command(int code) {
        this.code = code;
    }

    public static Command get(int code) {
        return commands.get(code);
    }

    private static final Map<Integer, Command> commands = new HashMap<>();

    static {
        for (Command cmd : values()) {
            if (commands.containsKey(cmd.code)) {
                throw new RuntimeException("命令【" + cmd.code + "】重复");
            }
            commands.put(cmd.code, cmd);
        }
    }

}
