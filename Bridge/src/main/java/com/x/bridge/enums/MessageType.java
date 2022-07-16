package com.x.bridge.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @author AD
 * @date 2022/7/16 15:05
 */
public enum MessageType {
    socket(1),
    command(2);

    public static MessageType get(int code) {
        return types.get(code);
    }

    private static final Map<Integer, MessageType> types = new HashMap<>();
    public final int code;

    private MessageType(int code) {
        this.code = code;
    }

    static {
        for (MessageType type : values()) {
            types.put(type.code, type);
        }
    }
}
