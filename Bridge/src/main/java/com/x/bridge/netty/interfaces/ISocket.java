package com.x.bridge.netty.interfaces;

import com.x.bridge.netty.common.IService;

/**
 * 服务器接口
 * @author AD
 * @date 2022/6/21 12:30
 */
public interface ISocket extends IService {

    boolean isServerMode();

}
