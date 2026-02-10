package org.example.client.init;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.example.client.handle.BaseHandle;
import org.example.client.handle.ControlHandle;
import org.example.common.handle.BoJsonOutboundHandle;
import org.example.common.handle.JsonToBOInboundHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@Sharable
public class InitPipeline extends ChannelInitializer {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast(applicationContext.getBean(BaseHandle.class));
        ch.pipeline().addLast(applicationContext.getBean(DelimiterBasedFrameDecoder.class));
        ch.pipeline().addLast(applicationContext.getBean(StringDecoder.class));
        ch.pipeline().addLast(applicationContext.getBean(StringEncoder.class));
        ch.pipeline().addLast(applicationContext.getBean(JsonToBOInboundHandle.class));
        ch.pipeline().addLast(applicationContext.getBean(BoJsonOutboundHandle.class));
        ch.pipeline().addLast(applicationContext.getBean(ControlHandle.class));
    }
}
