package org.example.client.init;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.example.client.handle.CheckHandle;
import org.example.client.handle.ControlHandle;
import org.example.common.constant.CommonConstant;
import org.example.common.handle.BoJsonOutboundHandle;
import org.example.common.handle.JsonToBOInboundHandle;

import java.nio.charset.StandardCharsets;

@Sharable
public class InitPipeline extends ChannelHandlerAdapter {

    @Override
    public void handlerAdded(ChannelHandlerContext ch) throws Exception {
        int maxLength = 1024 * 1024;
        ByteBuf byteBuf = Unpooled.copiedBuffer(CommonConstant.MESSAGE_END_MARK.getBytes(StandardCharsets.UTF_8));
        ch.pipeline().addLast(new DelimiterBasedFrameDecoder(maxLength, byteBuf));
        ch.pipeline().addLast(new StringDecoder(StandardCharsets.UTF_8));
        ch.pipeline().addLast(new StringEncoder(StandardCharsets.UTF_8));
        ch.pipeline().addLast(new JsonToBOInboundHandle());
        ch.pipeline().addLast(new BoJsonOutboundHandle());
        ch.pipeline().addLast(new CheckHandle());
        ch.pipeline().addLast(new ControlHandle());

    }

}
