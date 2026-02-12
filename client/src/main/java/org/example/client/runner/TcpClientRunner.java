package org.example.client.runner;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.example.client.context.ApplicationContextGatherUtils;
import org.example.common.enume.OperateEnum;
import org.example.common.message.Operate;
import org.example.common.utils.SystemMessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Scanner;

@Component
public class TcpClientRunner {

    @Autowired
    @Qualifier("tcpBootstrap")
    private Bootstrap tcpBootstrap;

    @Value("${netty.server.host:}")
    private String host;

    @Value("${netty.server.port:-1}")
    private int port;

    public void runTcpClient(InetSocketAddress socketAddress) throws Exception {
        String host = socketAddress.getAddress().getHostAddress();
        runTcpClient(host, socketAddress.getPort());
    }

    public void runTcpClient() throws Exception {
        if (host.isEmpty() || port == -1) {
            throw new RuntimeException("请配置服务器地址和端口");
        }

        runTcpClient(host, port);
    }

    public void runTcpClient(String hostAddress, int p) throws Exception {
        ChannelFuture future = tcpBootstrap.connect(hostAddress, p).sync();

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
        channel.writeAndFlush(SystemMessageUtils.stringMessage("天王盖地虎"));
    }
}
