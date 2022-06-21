package com.x.bridge.util;

import com.x.doraemon.Arrayx;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Netty Channel帮助类
 * @author AD
 * @date 2022/6/21 16:01
 */
public class ChannelHelper {
    
    private static final Map<String, ChnInfo> chnInfos = new ConcurrentHashMap<>();
    
    public static ChnInfo getChannelInfo(ChannelHandlerContext ctx) {
        Channel chn = ctx.channel();
        String local = chn.localAddress().toString().substring(1);
        String remote = chn.remoteAddress().toString().substring(1);
        String key = genKey(remote, local);
        if (chnInfos.containsKey(key)) {
            return chnInfos.get(key);
        }
        synchronized (chnInfos) {
            if (!chnInfos.containsKey(key)) {
                ChnInfo ci = new ChnInfo();
                ci.setRemote(remote);
                ci.setLocal(local);
                String[] remotes = remote.split(":");
                String[] locals = local.split(":");
                try {
                    ci.setRemoteHost(InetAddress.getByName(remotes[0]).getHostAddress());
                    ci.setRemotePort(Integer.parseInt(remotes[1]));
                    ci.setLocalHost(InetAddress.getByName(locals[0]).getHostAddress());
                    ci.setLocalPort(Integer.parseInt(locals[1]));
                    chnInfos.put(key, ci);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return chnInfos.get(key);
    }
    
    public static byte[] readData(ByteBuf buf) {
        if (buf != null) {
            int len = buf.readableBytes();
            byte[] data = new byte[len];
            buf.readBytes(data);
            return data;
        }
        return Arrayx.EMPTY_BYTE_ARRAY;
    }
    
    private static String genKey(String remote, String local) {
        return remote + "|" + local;
    }
    
}
