package org.example.service.init.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.example.common.constant.CommonConstant;
import org.example.common.handle.BoJsonOutboundHandle;
import org.example.common.handle.JsonToBOInboundHandle;
import org.example.common.utils.IpUtis;
import org.example.service.context.ApplicationContextGatherUtils;
import org.example.service.handle.CheckHandle;
import org.example.service.handle.ControlHandle;

import java.nio.charset.StandardCharsets;

@ChannelHandler.Sharable
public class InitPipeline extends ChannelInboundHandlerAdapter {

    @Override
    public void handlerAdded(ChannelHandlerContext ch) throws Exception {
        int maxLength = 1024 * 1024 * 1024;
        ByteBuf byteBuf = Unpooled.copiedBuffer(CommonConstant.MESSAGE_END_MARK.getBytes(StandardCharsets.UTF_8));
        ch.pipeline().addLast(new DelimiterBasedFrameDecoder(maxLength, byteBuf));
        ch.pipeline().addLast(new StringDecoder(StandardCharsets.UTF_8));
        ch.pipeline().addLast(new StringEncoder(StandardCharsets.UTF_8));
        ch.pipeline().addLast(new JsonToBOInboundHandle());
        ch.pipeline().addLast(new BoJsonOutboundHandle());
        ch.pipeline().addLast(new CheckHandle());
        ch.pipeline().addLast(new ControlHandle());
        if (ch.channel().localAddress() != null) {
            System.out.println(IpUtis.getIp(ch) + "连接");
        }
        ApplicationContextGatherUtils.nettyApplicationContext().addNoCheckList(ch.channel());
        super.handlerAdded(ch);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        ApplicationContextGatherUtils.nettyApplicationContext().removeNoCheckList(ctx.channel());
        ApplicationContextGatherUtils.nettyApplicationContext().removePokerChannel(ctx.channel());
        super.handlerRemoved(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        System.err.println(IpUtis.getIp(ctx)+"断开连接");
        //cause.printStackTrace();
        ctx.close();
    }

}
