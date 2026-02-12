package org.example.common.handle;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.example.common.message.BaseBo;
import org.example.common.message.GameRound;
import org.example.common.message.JsonObjectInfo;
import org.example.common.message.Player;
import org.example.common.constant.CommonConstant;
import org.example.common.enume.GameRoundStatusEnum;
import org.example.common.utils.JSONUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
@Scope("prototype")
public class BoJsonOutboundHandle extends ChannelOutboundHandlerAdapter {

    @Autowired
    private MessageJsonProcessor messageJsonProcessor;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        //System.err.println("接收出站消息：" +  msg);
        if (msg instanceof BaseBo) {
            try {
                String msgJson = messageJsonProcessor.process((BaseBo) msg);
                JsonObjectInfo jsonObjectInfo = new JsonObjectInfo();
                jsonObjectInfo.setClassInfo(msg.getClass().getName());
                jsonObjectInfo.setJsonStr(msgJson);

                String jsonStr = JSONUtils.toJSONString(jsonObjectInfo) + CommonConstant.MESSAGE_END_MARK;
                //System.err.println("发送出站消息：" +  jsonStr);
                ctx.write(jsonStr, promise);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            System.err.println("出站消息未知类型");
            ctx.write(msg, promise);
        }
    }



}