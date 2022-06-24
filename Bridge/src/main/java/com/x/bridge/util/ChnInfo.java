package com.x.bridge.util;

import lombok.Getter;
import lombok.Setter;

/**
 * 通道信息
 * @author AD
 * @date 2022/6/21 16:09
 */
@Getter
@Setter
public class ChnInfo {
    
    // ---------------------- 远端信息 ----------------------
    private String remote;
    
    private String remoteHost;
    
    private int remotePort;
    
    // ---------------------- 本地信息 ----------------------
    
    private String local;
    
    private String localHost;
    
    private int localPort;
    
    // ---------------------- App Server信息 ----------------------
    private String appServerHost;
    
    private int appServerPort;
    
}
