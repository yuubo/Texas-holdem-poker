package org.example.client.init;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.example.common.factory.EventLoopGroupFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitConfig {

    @Autowired
    private EventLoopGroupFactory eventLoopGroupFactory;

    @Bean
    public EventLoopGroup eventLoopGroup(@Value("${netty.eventLoopGroup.threadCount:2}") int threadCount) {
        return eventLoopGroupFactory.create(2);
    }

    @Bean
    public Bootstrap udpBootstrap(EventLoopGroup eventLoopGroup) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new InitUDPPipeline());
        return bootstrap;
    }

    @Bean
    public Bootstrap tcpBootstrap(EventLoopGroup eventLoopGroup) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                //.option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new InitPipeline());
        return bootstrap;
    }
}
