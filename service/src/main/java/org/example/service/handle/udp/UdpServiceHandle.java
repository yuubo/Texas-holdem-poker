package org.example.service.handle.udp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.example.common.warp.UdpMessage;

public class UdpServiceHandle extends SimpleChannelInboundHandler<UdpMessage> {

/*    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("收到消息：" + msg);
        if (msg.equals("hello")) {
            ctx.writeAndFlush("hello");
        }
    }*/

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, UdpMessage msg) throws Exception {
        System.out.println("收到消息：" + msg.getMessage());

        if (msg.getMessage().equals("hello")) {
            System.out.println("回应信息");

            ctx.write(msg);
        }
    }
}
