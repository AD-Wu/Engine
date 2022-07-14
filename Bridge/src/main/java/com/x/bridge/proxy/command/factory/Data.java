package com.x.bridge.proxy.command.factory;

import com.x.bridge.bean.Message;
import com.x.bridge.proxy.command.core.Command;
import com.x.bridge.proxy.command.core.ICommand;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;

/**
 * @author AD
 * @date 2022/7/14 10:36
 */
@Log4j2
public class Data implements ICommand {

    private final ChannelHandlerContext chn;

    public Data(ChannelHandlerContext chn) {
        this.chn = chn;
    }

    @Override
    public void execute(Message msg) {
        chn.write(msg.getData());
        log.info("会话【{}】发送第【{}】条数据【{}】", msg.getAppClient(), msg.getSeq(), msg.getData().length);
    }

    @Override
    public int getCode() {
        return Command.data.code;
    }
}
