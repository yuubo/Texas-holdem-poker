package org.example.service.init.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import org.example.common.handle.udp.DatagramPacketToStrHandle;
import org.example.common.handle.udp.StrToDatagramPacketHandle;
import org.example.service.handle.udp.UdpServiceHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class InitUDPPipeline extends ChannelInitializer {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast(new DatagramPacketToStrHandle());
        ch.pipeline().addLast(new StrToDatagramPacketHandle());
        ch.pipeline().addLast(applicationContext.getBean(UdpServiceHandle.class));
    }
}
