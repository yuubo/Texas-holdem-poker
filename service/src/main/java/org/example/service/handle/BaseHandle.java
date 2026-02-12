package org.example.service.handle;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.example.common.utils.SystemMessageUtils;
import org.example.service.channel.PokerChannel;
import org.example.service.channel.PokerChannelStatusEnum;
import org.example.service.context.ApplicationContextGatherUtils;
import org.example.service.context.NettyApplicationContext;
import org.example.service.context.PokerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.SocketException;

@Component
@Scope("prototype")
public class BaseHandle extends ChannelHandlerAdapter {

    @Autowired
    @Lazy
    private NettyApplicationContext nettyApplicationContext;

    @Autowired
    private PokerContext pokerContext;

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
            nettyApplicationContext.removePokerChannel(ctx.channel());
            pokerContext.disconnect(pokerChannel);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof SocketException) {
            PokerChannel pokerChannel = nettyApplicationContext.getPokerChannel(ctx.channel());
            System.out.println("玩家" + pokerChannel.getUser().getName() + "离开");
            Object[] arg = {pokerChannel.getUser().getName()};
            nettyApplicationContext.getPokerChannel(ctx.channel().eventLoop()).stream().forEach(p -> {
                if (p.getChannel() != pokerChannel) {
                    p.getChannel().writeAndFlush(SystemMessageUtils.messageSource("service.gameround.hint.h", arg));
                }
            });
        } else {
            cause.printStackTrace();
        }
        ctx.close();
    }

}
