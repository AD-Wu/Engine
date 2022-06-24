package com.x.bridge.transport;

import com.x.bridge.proxy.core.Message;

import java.util.Queue;

/**
 * 传输管理者
 * @author AD
 * @date 2022/6/22 21:30
 */
public class TransportManager {
    
    
    private  Queue<Message> connectRequests;
    private  Queue<Message> connectResponses;
    private  Queue<Message> datas;
}
