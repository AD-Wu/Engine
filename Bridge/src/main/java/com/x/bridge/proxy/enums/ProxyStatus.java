package com.x.bridge.proxy.enums;

/**
 * 代理状态
 * @author AD
 * @date 2022/6/22 22:13
 */
public enum ProxyStatus {
    creating,
    created,
    createFail,
    syncStart,
    running,
    syncSession,
    stopped,
    error;
}
