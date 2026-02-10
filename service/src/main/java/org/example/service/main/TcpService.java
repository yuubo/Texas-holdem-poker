package org.example.service.main;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.example.service.init.netty.InitPipeline;

public class TcpService {
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) //设置服务器通道
                    .option(ChannelOption.SO_BACKLOG, 128) //设置TaskQueue数量
                    .childOption(ChannelOption.SO_KEEPALIVE, true) //设置保持连接
                    .childHandler(new InitPipeline())
                    //.handler(new BoosHandle())
            ;

            System.out.println("Tcp服务已启动...");

            ChannelFuture channelFuture = serverBootstrap.bind(8070).sync();
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        System.out.println("绑定端口8070成功");
                    }
                }
            });

            //对关闭的通道进行监听
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) {
        System.out.println(1 / 8);
    }
}
