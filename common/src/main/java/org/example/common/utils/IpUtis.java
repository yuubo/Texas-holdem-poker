package org.example.common.utils;

import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;

public class IpUtis {

    public static String getIp(ChannelHandlerContext ctx) {
        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        if (address == null) {
            return null;
        }
        String ip = address.getAddress().getHostAddress()+":"+address.getPort();
        return ip;
    }
}
