package com.x.bridge.enums;

/**
 * 代理状态
 * @author AD
 * @date 2022/6/22 22:13
 */
public enum ProxyStatus {
    sync,
    running,
    stopped,
    socketServerError,
    sessionError,
    transportError;
}
