package com.x.bridge.proxy.core;

import lombok.Getter;
import lombok.Setter;

/**
 * 数据载体
 * @author AD
 * @date 2022/6/21 15:26
 */
@Getter
@Setter
public class Message {
    
    private String host;
    
    private int port;
    
    private int seq;
    
    private int cmd;
    
    private byte[] data;
    
}
