package org.example.service.init.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.example.common.handle.udp.DatagramPacketToStrHandle;
import org.example.common.handle.udp.StrToDatagramPacketHandle;
import org.example.service.handle.udp.UdpServiceHandle;

public class InitUDPPipeline extends ChannelInboundHandlerAdapter {

    @Override
    public void handlerAdded(ChannelHandlerContext ch) throws Exception {
        ch.pipeline().addLast(new DatagramPacketToStrHandle());
        ch.pipeline().addLast(new StrToDatagramPacketHandle());
        ch.pipeline().addLast(new UdpServiceHandle());
    }

}
