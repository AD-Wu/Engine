package com.x.bridge.proxy.cmd;

import com.x.bridge.bean.Message;
import com.x.bridge.proxy.ProxyManager;
import com.x.bridge.proxy.core.IProxyService;
import com.x.bridge.proxy.core.ProxyConfig;
import com.x.bridge.proxy.core.socket.Session;
import com.x.bridge.proxy.enums.MsgType;
import com.x.bridge.proxy.enums.ProxyStatus;
import com.x.bridge.proxy.core.ProxyClient;
import com.x.doraemon.Arrayx;
import com.x.doraemon.Jsons;
import lombok.extern.log4j.Log4j2;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * 代理命令
 * @author AD
 * @date 2022/7/17 15:51
 */
@Log4j2
public enum Cmd implements ICmd {

    createProxyClient(20){
        @Override
        public void execute(Message msg, IProxyService proxy) {
            String name = msg.getProxyName();
            String s = new String(msg.getData(),StandardCharsets.UTF_8);
            ProxyConfig conf = Jsons.fromJson(s, ProxyConfig.class);
            ProxyClient client = new ProxyClient(conf);
            Message resp = new Message();
            resp.setProxyName(name);
            resp.setType(MsgType.resp.code);
            if(ProxyManager.putProxy(client)){
                resp.setCmd(createProxyClientSuccess.code);
                try {
                    ProxyManager.sendMessage(name,resp);
                } catch (Exception e) {
                    e.printStackTrace();
                    ProxyManager.removeProxy(name);
                }
            }else{
                resp.setCmd(createProxyClientFail.code);
                try {
                    ProxyManager.sendMessage(name,resp);
                } catch (Exception e) {
                    e.printStackTrace();
                    ProxyManager.removeProxy(name);
                }
            }
        }
    },
    createProxyClientSuccess(8020){
        @Override
        public void execute(Message msg, IProxyService proxy) {
            proxy.status(ProxyStatus.created);
        }
    },
    createProxyClientFail(80201){
        @Override
        public void execute(Message msg, IProxyService proxy) {
            proxy.status(ProxyStatus.createFail);
            ProxyManager.removeProxy(msg.getProxyName());
        }
    },
    sync(21) {
        @Override
        public void execute(Message msg, IProxyService proxy) {
            byte[] data = msg.getData();
            String[] valids = null;
            if (data != null && data.length > 0) {
                valids = new String(data, StandardCharsets.UTF_8).split(",");
            } else {
                // 没有socket连接,清空所有传输媒介的数据
                proxy.getBus().clear();
            }
            // 同步会话缓存
            Set<String> invalids = Arrays.stream(proxy.clients()).collect(toSet());
            if (Arrayx.isNotEmpty(valids)) {
                invalids.removeAll(Arrays.stream(valids).collect(toSet()));
            }
            invalids.stream().forEach(invalid -> {
                Session session = proxy.removeSession(invalid);
                if (session != null) {
                    session.close();
                }
            });

            // 写入响应消息
            Message resp = new Message();
            resp.setProxyName(msg.getProxyName());
            resp.setCmd(Cmd.syncSuccess.code);
            resp.setType(MsgType.resp.code);
            try {
                ProxyManager.sendMessage(msg.getProxyName(),resp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    },
    syncSuccess(8021) {
        @Override
        public void execute(Message msg, IProxyService proxy) {
            proxy.syncSuccess();
        }
    };

    @Override
    public int code() {
        return this.code;
    }

    public final int code;

    private Cmd(int code) {
        this.code = code;
    }

    public static ICmd get(int code) {
        return commands.get(code);
    }

    public static boolean register(int code, ICmd cmd) {
        if (commands.containsKey(code)) {
            throw new RuntimeException("命令【" + code + "】重复");
        }
        commands.put(code, cmd);
        return true;
    }

    private static final Map<Integer, ICmd> commands = new HashMap<>();

    static {
        for (Cmd cmd : values()) {
            if (commands.containsKey(cmd.code)) {
                throw new RuntimeException("命令【" + cmd.code + "】重复");
            }
            commands.put(cmd.code, cmd);
        }
    }
}
