package org.example.common.handle.udp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.example.common.warp.UdpMessage;

import java.nio.charset.StandardCharsets;

public class DatagramPacketToStrHandle extends SimpleChannelInboundHandler<DatagramPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        byte[] data = new byte[msg.content().readableBytes()];
        msg.content().readBytes(data);
        String s = new String(data, StandardCharsets.UTF_8);
        UdpMessage udpMessage = new UdpMessage();
        udpMessage.setMessage(s);
        udpMessage.setSocketAddress(msg.sender());
        ctx.fireChannelRead(udpMessage);
    }
}
