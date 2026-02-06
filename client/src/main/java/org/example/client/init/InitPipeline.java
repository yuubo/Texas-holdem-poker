package org.example.client.init;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.example.client.handle.CheckHandle;
import org.example.client.handle.ControlHandle;
import org.example.common.constant.CommonConstant;
import org.example.common.handle.BoJsonOutboundHandle;
import org.example.common.handle.JsonToBOInboundHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@Sharable
public class InitPipeline extends ChannelInitializer {

    @Autowired
    private DelimiterBasedFrameDecoder delimiterBasedFrameDecoder;

    @Autowired
    private StringDecoder stringDecoder;

    @Autowired
    private StringEncoder stringEncoder;

    @Autowired
    private JsonToBOInboundHandle jsonToBOInboundHandle;

    @Autowired
    private BoJsonOutboundHandle boJsonOutboundHandle;

    @Autowired
    private CheckHandle checkHandle;

    @Autowired
    private ControlHandle controlHandle;


    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast(delimiterBasedFrameDecoder);
        ch.pipeline().addLast(stringDecoder);
        ch.pipeline().addLast(stringEncoder);
        ch.pipeline().addLast(jsonToBOInboundHandle);
        ch.pipeline().addLast(boJsonOutboundHandle);
        ch.pipeline().addLast(checkHandle);
        ch.pipeline().addLast(controlHandle);
    }
}
