package org.example.service.handle.udp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.example.common.warp.UdpMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class UdpServiceHandle extends SimpleChannelInboundHandler<UdpMessage> {

    @Value("${netty.service.tcp.port}")
    private int tcpPort;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, UdpMessage msg) throws Exception {

        if (msg.getMessage().equals("hello")) {
            msg.setTcpPort(tcpPort);
            ctx.writeAndFlush(msg);
        }
    }
}
