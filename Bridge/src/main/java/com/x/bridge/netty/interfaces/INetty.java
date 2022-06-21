package com.x.bridge.netty.interfaces;

/**
 * 服务器接口
 * @author AD
 * @date 2022/6/21 12:30
 */
public interface INetty {
    
    String name();
    
    boolean isServerMode();
    
    boolean start();
    
    void stop();
    
}
