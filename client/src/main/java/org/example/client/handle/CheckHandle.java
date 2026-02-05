package org.example.client.handle;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.example.client.context.ApplicationContextGatherUtils;
import org.example.common.bo.BaseBo;
import org.example.common.bo.Message;
import org.example.common.utils.SystemMessageUtils;

public class CheckHandle extends SimpleChannelInboundHandler<BaseBo> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BaseBo msg) throws Exception {
        //System.out.println("检查服务器认证数据" +  msg);
        if (!ApplicationContextGatherUtils.clientApplicationContext().isCkeck) {
            if (msg instanceof Message) {
                Message message = (Message) msg;
                if (message.getMessage().equals("宝塔镇河妖")) {
                    System.out.println("服务器认证成功, 名称:" + message.getUser().getName());
                    ApplicationContextGatherUtils.clientApplicationContext().isCkeck = true;
                    ApplicationContextGatherUtils.clientApplicationContext().setUser(message.getUser());
                } else {
                    ctx.writeAndFlush(SystemMessageUtils.stringMessage("服务器认证失败", null));
                    ctx.close();
                }
            }

        } else {
            ctx.fireChannelRead(msg);
        }
    }
}
