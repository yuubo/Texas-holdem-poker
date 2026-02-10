package org.example.client.main;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.example.client.init.InitPipeline;
import org.example.common.utils.SystemMessageUtils;

import java.util.Scanner;

public class TcpClient {

    public void run(String host, int port) {
        EventLoopGroup clientEventLoopGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(clientEventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new InitPipeline());

            System.out.println("开始连接"+host+":"+8070);
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8070 ).sync();

            Channel channel = channelFuture.channel();

            new Thread(() -> {
                Scanner scanner = new Scanner(System.in);
                while (true) {
                    String nextLine = scanner.nextLine();
                    if ("q".equalsIgnoreCase(nextLine)) {
                        channel.close();
                        break;
                    }

                    channel.writeAndFlush(SystemMessageUtils.stringMessage(nextLine));
                }
            }, "input").start();

            ChannelFuture closeFuture = channel.closeFuture();
            closeFuture.sync();

            closeFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    System.out.println("连接已关闭");
                    clientEventLoopGroup.shutdownGracefully();
                }
            });

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            clientEventLoopGroup.shutdownGracefully();
        }
    }
}
