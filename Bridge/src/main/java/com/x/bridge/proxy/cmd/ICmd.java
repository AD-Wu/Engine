package com.x.bridge.proxy.cmd;

import com.x.bridge.bean.Msg;
import com.x.bridge.proxy.core.IProxyService;

/**
 * 代理命令
 * @author AD
 * @date 2022/7/17 13:29
 */
public interface ICmd {
    
    void execute(Msg msg, IProxyService proxy);
    
    int code();
}
