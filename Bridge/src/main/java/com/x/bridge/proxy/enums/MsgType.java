package com.x.bridge.proxy.enums;

/**
 * 消息类型
 * @author AD
 * @date 2022/7/17 18:55
 */
public enum MsgType {
    req(1),
    resp(80);
    
    public final int code;
    
    private MsgType(int code) {
        this.code = code;
    }
    
}
