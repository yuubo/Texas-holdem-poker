package org.example.common.handle;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.example.common.bo.JsonObjectInfo;
import org.example.common.utils.JSONUtils;

public class JsonToBOInboundHandle extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.err.println("接收入站消息：" +  msg);
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