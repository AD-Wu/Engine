package com.x.bridge.proxy.interfaces;

import com.x.bridge.netty.interfaces.INetty;
import com.x.bridge.proxy.core.Replier;

/**
 * 代理服务器接口
 * @author AD
 * @date 2022/6/21 14:52
 */
public interface IProxy extends INetty {
    
    default boolean isAccept(String host){
        return true;
    }
    
    Replier getReplier(String appClient);
    
}
