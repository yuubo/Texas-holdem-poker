package org.example.service.handle.udp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.example.common.warp.UdpMessage;
import org.springframework.stereotype.Component;

public class UdpServiceHandle extends SimpleChannelInboundHandler<UdpMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, UdpMessage msg) throws Exception {

        if (msg.getMessage().equals("hello")) {
            ctx.writeAndFlush(msg);
        }
    }
}
