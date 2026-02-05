package org.example.service.handle;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.example.common.bo.Message;
import org.example.common.bo.Operate;
import org.example.common.channel.PokerChannel;
import org.example.common.enume.GameRoundStatusEnum;
import org.example.common.enume.OperateEnum;
import org.example.common.utils.SystemMessageUtils;
import org.example.service.context.ApplicationContextGatherUtils;
import org.example.service.context.PokerContext;

import java.net.InetSocketAddress;
import java.util.List;

public class ControlHandle extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Message) {
            messageHandle(ctx, (Message) msg);
        } else if (msg instanceof Operate) {
            operateHandle(ctx, (Operate) msg);
        } else {
            super.channelRead(ctx, msg);
        }

    }

    private void messageHandle(ChannelHandlerContext ctx, Message msg) {
        ctx.channel().eventLoop().execute(() -> {
            List<PokerChannel> pokerChannelList = ApplicationContextGatherUtils.nettyApplicationContext().getPokerChannel();
            pokerChannelList.forEach(pokerChannel -> {
                if (!pokerChannel.getChannel().equals(ctx.channel())) {
                    pokerChannel.getChannel().writeAndFlush(msg);
                }
            });
        });
    }

    private void operateHandle(ChannelHandlerContext ctx, Operate operate) {
        if (operate.getOperate() == OperateEnum.START.getOperate()) {
            if (ApplicationContextGatherUtils.pokerContext() == null) {
                PokerContext pokerContext = new PokerContext();
                pokerContext.start();
            } else if (ApplicationContextGatherUtils.pokerContext().getGameRound().getStatus() == GameRoundStatusEnum.FINISH.getStatus()) {
                ApplicationContextGatherUtils.pokerContext().start();
            } else {
                ctx.channel().writeAndFlush(SystemMessageUtils.stringMessage("游戏已开始", null));
            }

        } else {
            PokerChannel pokerChannel = ApplicationContextGatherUtils.nettyApplicationContext().getPokerChannel(ctx.channel());
            ApplicationContextGatherUtils.pokerContext().operateHandle(operate, pokerChannel);
        }

    }
}
