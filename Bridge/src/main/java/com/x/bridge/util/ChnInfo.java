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
    
    private String remote;
    
    private String remoteHost;
    
    private int remotePort;
    
    private String local;
    
    private String localHost;
    
    private int localPort;
    
    
    
}
