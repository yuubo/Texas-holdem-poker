package org.example.service.handle;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.EventExecutor;
import org.example.common.message.BaseBo;
import org.example.common.message.Message;
import org.example.common.message.Operate;
import org.example.common.message.User;
import org.example.common.enume.GameRoundStatusEnum;
import org.example.common.enume.OperateEnum;
import org.example.common.utils.SystemMessageUtils;
import org.example.common.utils.UserNameUtil;
import org.example.service.channel.PokerChannel;
import org.example.service.context.ApplicationContextGatherUtils;
import org.example.service.context.NettyApplicationContext;
import org.example.service.context.PokerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Spliterator;

@Component
@Scope("prototype")
public class ControlHandle extends SimpleChannelInboundHandler<BaseBo> {

    @Autowired
    private PokerContext pokerContext;

    @Autowired
    @Lazy
    private NettyApplicationContext nettyApplicationContext;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, BaseBo msg) throws Exception {

        if (nettyApplicationContext.noCheckContains(ctx.channel())) {
            checkChanle(ctx, msg);
        } else if (msg instanceof Message) {
            messageHandle(ctx, (Message) msg);
        } else if (msg instanceof Operate) {
            PokerChannel pokerChannel = nettyApplicationContext.getPokerChannel(ctx.channel());
            pokerContext.operateHandle((Operate) msg, pokerChannel);
        } else {
            super.channelRead(ctx, msg);
        }

    }

    private void checkChanle(ChannelHandlerContext ctx, BaseBo msg) {
        System.out.println("验证连接" + ctx.channel().remoteAddress() + ":" + msg);
        if (msg instanceof Message) {
            Message message = (Message) msg;
            if (message.getMessage().equals("天王盖地虎")) {
                PokerChannel pokerChannel = nettyApplicationContext.addPokerChanne(ctx.channel());
                if (pokerChannel == null) {
                    ctx.writeAndFlush(SystemMessageUtils.stringMessage("服务器连接已满"));
                    ctx.close();
                } else {
                    System.out.println("回复验证");
                    ctx.writeAndFlush(SystemMessageUtils.stringMessage(new User(UserNameUtil.getUserName()),"宝塔镇河妖"));
                    nettyApplicationContext.removeNoCheckList(ctx.channel());
                    pokerContext.addPlayer(pokerChannel);
                }

            } else {
                ctx.writeAndFlush(SystemMessageUtils.stringMessage("验证失败"));
                ctx.close();
            }
        } else {
            ctx.close();
        }

    }

    private void messageHandle(ChannelHandlerContext ctx, Message msg) {
        ctx.channel().eventLoop().execute(() -> {
            List<PokerChannel> pokerChannelList = nettyApplicationContext.getPokerChannel(ctx.channel().eventLoop());
            pokerChannelList.forEach(pokerChannel -> {
                if (!pokerChannel.getChannel().equals(ctx.channel())) {
                    pokerChannel.getChannel().writeAndFlush(msg);
                }
            });
        });
    }

}
