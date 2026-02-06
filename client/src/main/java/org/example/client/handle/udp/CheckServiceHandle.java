package org.example.client.handle.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.example.client.context.ApplicationContextGatherUtils;
import org.example.client.init.InitPipeline;
import org.example.common.bo.Operate;
import org.example.common.enume.OperateEnum;
import org.example.common.utils.SystemMessageUtils;
import org.example.common.warp.UdpMessage;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Scanner;

@Component
public class CheckServiceHandle extends SimpleChannelInboundHandler<UdpMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, UdpMessage msg) throws Exception {
        System.out.println("检查信息：" + msg.getMessage());

        if (!ApplicationContextGatherUtils.clientApplicationContext().isConnect()) {
            if (msg.getMessage().equals("hello")) {
                System.out.println("验证成功，开始连接TCP服务器");
                ApplicationContextGatherUtils.clientApplicationContext().setConnect(true);
                runTcpClient((InetSocketAddress) msg.getSocketAddress());
                ApplicationContextGatherUtils.clientApplicationContext().setChannel(ctx.channel());
            } else {
                if (QueryService.getSearchFrequency() < 5) {
                    ctx.channel().eventLoop().execute(QueryService.queryServiceFactory(ctx.channel()));
                } else {
                    System.out.println("服务器搜索失败");
                    ctx.channel().close();
                }
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private void runTcpClient(InetSocketAddress socketAddress) throws InterruptedException {
        String hostAddress = socketAddress.getAddress().getHostAddress();

        Thread.ofVirtual().start(() -> {
            ChannelFuture future = null;
            try {
                /* Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(new NioEventLoopGroup())
                        .channel(NioSocketChannel.class)
                        .handler(new InitPipeline());
                System.out.println("开始连接TCP服务器: " + hostAddress);
                future = bootstrap.connect(hostAddress, 8081).sync(); */
                Bootstrap tcpBootstrap = ApplicationContextGatherUtils.applicationContext().getBean("tcpBootstrap", Bootstrap.class);
                future = tcpBootstrap.connect(hostAddress, 8081).sync();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ApplicationContextGatherUtils.clientApplicationContext().setChannel(future.channel());
            Channel channel = future.channel();
            System.out.println("TCP服务器连接成功");
            Thread.ofVirtual().start(() -> {
                Scanner scanner = new Scanner(System.in);
                while (true) {
                    String nextLine = scanner.nextLine();
                    if ("@q".equalsIgnoreCase(nextLine)) {
                        channel.close();
                        break;
                    }

                    if (nextLine.startsWith("@")) {
                        operateHandle(nextLine.substring(1), channel);
                        continue;
                    }
                    channel.writeAndFlush(SystemMessageUtils.stringMessage(ApplicationContextGatherUtils.clientApplicationContext().getUser(), nextLine));
                }
            });

            sendSecurityCode(channel);
        });

        /*TcpClient tcpClient = new TcpClient();
        Thread thread = new Thread(() -> {
            System.out.println("启动TCP服务器线程");
            tcpClient.run(hostAddress, 8070);
        });
        thread.start();*/

    }

    private void operateHandle(String command, Channel channel) {
        try {
            String[] split = command.split("-");

            int operate = Integer.parseInt(split[0].trim());
            Operate operateBo = new Operate();
            operateBo.setOperate(operate);
            if (OperateEnum.getOperate(operate) != null) {
                if (split.length == 2) {
                    int score = Integer.parseInt(split[1].trim());
                    operateBo.setScore(score);
                }
                channel.writeAndFlush(operateBo);
            } else {
                System.out.println("输入错误");
            }
        } catch (NumberFormatException e) {
            System.out.println("输入错误");
        }
    }

    private void sendSecurityCode(Channel channel) {
        System.out.println("发送验证");
        channel.writeAndFlush(SystemMessageUtils.stringMessage("天王盖地虎", null));
    }
}
