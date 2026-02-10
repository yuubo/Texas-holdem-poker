package org.example.common.handle.udp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.DatagramPacket;
import org.example.common.warp.UdpMessage;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class StrToDatagramPacketHandle extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof UdpMessage) {
            UdpMessage udpMessage = (UdpMessage) msg;
            String s = udpMessage.getMessage();
            if (udpMessage.getTcpPort() != 0) {
                s += "@" + udpMessage.getTcpPort();
            }
            ByteBuf byteBuf = Unpooled.copiedBuffer(s.getBytes(StandardCharsets.UTF_8));
            ctx.writeAndFlush(new DatagramPacket(byteBuf, (InetSocketAddress) udpMessage.getSocketAddress()));
        } else {
            ctx.write(msg, promise);
        }
    }
}
