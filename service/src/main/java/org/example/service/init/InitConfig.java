package org.example.service.init;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.example.common.factory.EventLoopGroupFactory;
import org.example.service.init.netty.InitPipeline;
import org.example.service.init.netty.InitUDPPipeline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitConfig {

    @Autowired
    private EventLoopGroupFactory eventLoopGroupFactory;

    @Bean
    public EventLoopGroup boosGroup(@Value("${netty.boosGroup.loopCount:2}") int loopCount) {
        return eventLoopGroupFactory.create(loopCount);
    }

    @Bean
    public EventLoopGroup workerGroup() {
        return eventLoopGroupFactory.create();
    }

    @Bean
    public ServerBootstrap serverBootstrap(@Qualifier("boosGroup") EventLoopGroup boosGroup, @Qualifier("workerGroup") EventLoopGroup workerGroup) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boosGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new InitPipeline());
        return serverBootstrap;
    }

    @Bean
    public Bootstrap bootstrap(@Qualifier("boosGroup") EventLoopGroup boosGroup) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(boosGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new InitUDPPipeline());
        return bootstrap;
    }

}
