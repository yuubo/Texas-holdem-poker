package org.example.client.init;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import org.example.client.handle.udp.CheckServiceHandle;
import org.example.common.handle.udp.DatagramPacketToStrHandle;
import org.example.common.handle.udp.StrToDatagramPacketHandle;
import org.example.common.warp.UdpMessage;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class InitUDPPipeline extends ChannelHandlerAdapter {

    @Override
    public void handlerAdded(ChannelHandlerContext ch) throws Exception {
        ch.pipeline().addLast(new DatagramPacketToStrHandle());
        ch.pipeline().addLast(new StrToDatagramPacketHandle());
        ch.pipeline().addLast(new CheckServiceHandle());
    }

}
