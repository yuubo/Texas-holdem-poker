package org.example.service.handle;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.example.service.channel.PokerChannel;
import org.example.service.channel.PokerChannelStatusEnum;
import org.example.service.context.ApplicationContextGatherUtils;
import org.example.service.context.NettyApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class BaseHandle extends ChannelHandlerAdapter {

    @Autowired
    private NettyApplicationContext nettyApplicationContext;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        nettyApplicationContext.addNoCheckList(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        PokerChannel pokerChannel = nettyApplicationContext.getPokerChannel(ctx.channel());
        if (pokerChannel == null) {
            nettyApplicationContext.removeNoCheckList(ctx.channel());
        } else {
            pokerChannel.setStatus(PokerChannelStatusEnum.DISCONNECT.getStatus());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
