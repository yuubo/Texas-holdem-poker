package org.example.client.main;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.example.client.handle.udp.QueryService;
import org.example.client.init.InitUDPPipeline;

public class UdpClient {

    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new InitUDPPipeline())
                    //.handler(new CheckServiceHandle())
            ;

            //设置0为绑定随机端口
            ChannelFuture f = b.bind(0).sync();
            queryService(f);

            f.channel().closeFuture().sync().addListener(future -> {
                System.out.println("服务器搜索关闭");
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
            System.out.println("UDP客户端已关闭");
        }
    }

    private void queryService(ChannelFuture ch) {
        ch.channel().eventLoop().execute(QueryService.queryServiceFactory(ch.channel()));
    }

}
