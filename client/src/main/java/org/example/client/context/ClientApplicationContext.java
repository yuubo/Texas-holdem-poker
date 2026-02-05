package org.example.client.context;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import jakarta.annotation.PostConstruct;
import org.example.client.handle.udp.QueryService;
import org.example.common.bo.User;
import org.example.common.warp.UdpMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Component
public final class ClientApplicationContext implements ApplicationRunner {

    public volatile boolean isCkeck = false;

    private static Channel channel = null;

    private volatile static boolean isConnect = false;

    private User user;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    @Qualifier("udpBootstrap")
    private Bootstrap udpBootstrap;

    @PostConstruct
    public void init() {
        ApplicationContextGatherUtils.applicationContext = applicationContext;
        ApplicationContextGatherUtils.clientApplicationContext = this;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ChannelFuture future = udpBootstrap.bind(0).sync();
        System.out.println("启动UDP搜索服务");
        future.channel().eventLoop().execute(QueryService.queryServiceFactory(future.channel()));
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public boolean isConnect() {
        return isConnect;
    }

    public void setConnect(boolean connect) {
        isConnect = connect;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
