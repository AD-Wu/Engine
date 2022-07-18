package com.x.bridge.proxy.enums;

import com.x.bridge.proxy.core.IProxyService;
import com.x.bridge.bus.core.IReader;
import com.x.bridge.bus.core.IWriter;
import com.x.bridge.bus.factory.DBReader;
import com.x.bridge.bus.factory.DBWriter;

/**
 * @author AD
 * @date 2022/6/25 13:48
 */
public enum BusType {
    DB {
        @Override
        public IReader createReader(IProxyService proxy) {
            return new DBReader(proxy);
        }

        @Override
        public IWriter createWriter(IProxyService proxy) {
            return new DBWriter(proxy);
        }
    };

    public static BusType get(String mode) {
        BusType tm = valueOf(mode.toUpperCase());
        return tm;
    }

    public abstract IReader createReader(IProxyService proxy);

    public abstract IWriter createWriter(IProxyService proxy);

}
