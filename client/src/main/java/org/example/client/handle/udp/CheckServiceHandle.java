package org.example.client.handle.udp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.example.client.context.ApplicationContextGatherUtils;
import org.example.client.runner.TcpClientRunner;
import org.example.common.warp.UdpMessage;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetSocketAddress;

public class CheckServiceHandle extends SimpleChannelInboundHandler<UdpMessage> {

    private TcpClientRunner tcpClientRunner;

    public CheckServiceHandle(TcpClientRunner tcpClientRunner) {
        this.tcpClientRunner = tcpClientRunner;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, UdpMessage msg) throws Exception {
        if (!ApplicationContextGatherUtils.clientApplicationContext().isConnect()) {
            System.out.println("开始验证: "+ msg.getMessage());
            if (msg.getMessage().startsWith("hello@")) {
                System.out.println("验证成功，开始连接TCP服务器");
                ApplicationContextGatherUtils.clientApplicationContext().setConnect(true);
                InetSocketAddress socketAddress = (InetSocketAddress) msg.getSocketAddress();
                int port = Integer.valueOf(msg.getMessage().substring(6));
                tcpClientRunner.runTcpClient(socketAddress.getAddress().getHostAddress(), port);
                ApplicationContextGatherUtils.clientApplicationContext().setChannel(ctx.channel());
            } else {
                if (QueryService.getSearchFrequency() < 5) {
                    ctx.channel().eventLoop().execute(QueryService.queryService());
                } else {
                    System.out.println("服务器搜索失败");
                    ctx.channel().close();
                }
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

}
