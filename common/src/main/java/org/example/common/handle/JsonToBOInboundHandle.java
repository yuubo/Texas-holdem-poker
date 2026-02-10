package org.example.common.handle;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.example.common.message.JsonObjectInfo;
import org.example.common.utils.JSONUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class JsonToBOInboundHandle extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        if (msg instanceof String) {
            JsonObjectInfo jsonObjectInfo = JSONUtils.parseObject(msg, JsonObjectInfo.class);
            Class<?> clazz = Class.forName(jsonObjectInfo.getClassInfo());
            Object object = JSONUtils.parseObject(jsonObjectInfo.getJsonStr(), clazz);
            ctx.fireChannelRead(object);
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}