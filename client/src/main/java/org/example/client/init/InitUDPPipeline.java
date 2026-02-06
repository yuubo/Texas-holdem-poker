package org.example.client.init;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import org.example.client.handle.udp.CheckServiceHandle;
import org.example.common.handle.udp.DatagramPacketToStrHandle;
import org.example.common.handle.udp.StrToDatagramPacketHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InitUDPPipeline extends ChannelInitializer {

    @Autowired
    private DatagramPacketToStrHandle datagramPacketToStrHandle;

    @Autowired
    private StrToDatagramPacketHandle strToDatagramPacketHandle;

    @Autowired
    private CheckServiceHandle checkServiceHandle;

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast(datagramPacketToStrHandle);
        ch.pipeline().addLast(strToDatagramPacketHandle);
        ch.pipeline().addLast(checkServiceHandle);
    }

}
