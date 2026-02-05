package org.example.client.handle;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.example.client.print.ExportConsole;
import org.example.common.bo.Message;
import org.example.common.bo.Operate;
import org.example.common.bo.Player;
import org.example.common.enume.OperateEnum;

public class ControlHandle extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Message) {
            messageHandle((Message) msg, ctx.channel());
        } else if (msg instanceof Player) {
            ExportConsole.print((Player) msg);
        } else if(msg instanceof Operate) {
            ExportConsole.print((Operate) msg);
        }
    }

    private void messageHandle(Message massage, Channel channel) {
        System.out.println(massage.getUser().getName() + ":" + massage.getMessage());
    }

}