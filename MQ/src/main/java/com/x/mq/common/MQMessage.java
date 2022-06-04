package com.x.mq.common;

import java.io.Serializable;

/**
 * @author AD
 * @date 2022/3/24 12:40
 */
public class MQMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private byte[] body;

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
