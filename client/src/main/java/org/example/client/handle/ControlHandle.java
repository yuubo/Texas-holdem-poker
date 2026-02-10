package org.example.client.handle;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.example.client.context.ApplicationContextGatherUtils;
import org.example.client.context.ClientApplicationContext;
import org.example.client.print.ExportConsole;
import org.example.common.message.BaseBo;
import org.example.common.message.Message;
import org.example.common.message.Operate;
import org.example.common.message.Player;
import org.example.common.utils.SystemMessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class ControlHandle extends SimpleChannelInboundHandler<BaseBo> {

    @Autowired
    private ClientApplicationContext clientApplicationContext;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, BaseBo msg) throws Exception {
        if (!clientApplicationContext.isCkeck) {
            if (msg instanceof Message) {
                Message message = (Message) msg;
                if (message.getMessage().equals("宝塔镇河妖")) {
                    clientApplicationContext.isCkeck = true;
                    clientApplicationContext.setUser(message.getUser());
                    System.out.println("服务器认证成功, 名称:" + message.getUser().getName());
                }
            } else {
                ctx.writeAndFlush(SystemMessageUtils.stringMessage("服务器认证失败"));
                ctx.close();
            }
        } else if (msg instanceof Message) {
            messageHandle((Message) msg);
        } else if (msg instanceof Player) {
            ExportConsole.print((Player) msg);
        } else if(msg instanceof Operate) {
            ExportConsole.print((Operate) msg);
        }
    }

    private void messageHandle(Message massage) {
        System.out.println(massage.getUser().getName() + ":" + massage.getMessage());
    }

}