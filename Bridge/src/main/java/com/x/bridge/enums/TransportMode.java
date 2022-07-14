package com.x.bridge.enums;

import com.x.bridge.proxy.core.IProxy;
import com.x.bridge.transport.core.IReader;
import com.x.bridge.transport.core.IWriter;
import com.x.bridge.transport.factory.DBReader;
import com.x.bridge.transport.factory.DBWriter;

/**
 * @author AD
 * @date 2022/6/25 13:48
 */
public enum TransportMode {
    DB {
        @Override
        public IReader createReader(IProxy proxy) {
            return new DBReader(proxy);
        }

        @Override
        public IWriter createWriter(IProxy proxy) {
            return new DBWriter(proxy);
        }
    };

    public static TransportMode get(String mode) {
        TransportMode tm = valueOf(mode.toUpperCase());
        return tm;
    }

    public abstract IReader createReader(IProxy proxy);

    public abstract IWriter createWriter(IProxy proxy);

}
