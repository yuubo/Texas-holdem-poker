package org.example.service.handle;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import org.example.common.bo.BaseBo;
import org.example.common.bo.Message;
import org.example.common.channel.PokerChannel;
import org.example.common.utils.SystemMessageUtils;
import org.example.service.context.ApplicationContextGatherUtils;
import org.example.service.context.NettyApplicationContext;

public class CheckHandle extends SimpleChannelInboundHandler<BaseBo> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, BaseBo msg) throws Exception {
        System.out.println("验证连接" + ctx.channel().remoteAddress() + ":" + msg);
        if (ApplicationContextGatherUtils.nettyApplicationContext().noCheckContains(ctx.channel())) {
            System.out.println("验证连接" + ctx.channel().remoteAddress());
            if (msg instanceof Message) {
                Message message = (Message) msg;
                if (message.getMessage().equals("天王盖地虎")) {
                    PokerChannel pokerChannel = ApplicationContextGatherUtils.nettyApplicationContext().addPokerChanne(ctx.channel());
                    if (pokerChannel == null) {
                        ctx.writeAndFlush(SystemMessageUtils.stringMessage("服务器连接已满", null));
                        ctx.close();
                    } else {
                        System.out.println("回复验证");
                        ctx.writeAndFlush(SystemMessageUtils.stringMessage("宝塔镇河妖", pokerChannel));
                        ApplicationContextGatherUtils.nettyApplicationContext().removeNoCheckList(ctx.channel());
                        return;
                    }

                } else {
                    ctx.writeAndFlush(SystemMessageUtils.stringMessage("验证失败", null));
                    ctx.close();
                }
            } else {
                ctx.close();
            }

        } else {
            ctx.fireChannelRead(msg);
        }
    }
}
