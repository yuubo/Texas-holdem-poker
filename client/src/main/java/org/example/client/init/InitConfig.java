package org.example.client.init;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.example.client.handle.udp.CheckServiceHandle;
import org.example.client.runner.TcpClientRunner;
import org.example.common.constant.CommonConstant;
import org.example.common.factory.EventLoopGroupFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

@Configuration
public class InitConfig {

    @Bean
    @ConditionalOnProperty(name = "netty.client.udp.enabled", havingValue = "true")
    public CheckServiceHandle checkServiceHandle(TcpClientRunner tcpClientRunner) {
        return new CheckServiceHandle(tcpClientRunner);
    }

    @Bean
    @ConditionalOnBean(CheckServiceHandle.class)
    public InitUDPPipeline initUDPPipeline(CheckServiceHandle checkServiceHandle) {
        InitUDPPipeline initUDPPipeline = new InitUDPPipeline(checkServiceHandle);
        return initUDPPipeline;
    }

    @Bean
    public EventLoopGroup eventLoopGroup(@Value("${netty.eventLoopGroup.threadCount:2}") int threadCount,
                                         EventLoopGroupFactory eventLoopGroupFactory) {
        return eventLoopGroupFactory.create(2);
    }

    @Bean
    @ConditionalOnBean(InitUDPPipeline.class)
    public Bootstrap udpBootstrap(EventLoopGroup eventLoopGroup, InitUDPPipeline initUDPPipeline) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(initUDPPipeline);
        return bootstrap;
    }

    @Bean
    public Bootstrap tcpBootstrap(InitPipeline initPipeline) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                //.option(ChannelOption.SO_KEEPALIVE, true)
                .handler(initPipeline);
        return bootstrap;
    }

}
