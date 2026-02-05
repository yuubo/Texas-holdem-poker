package org.example.common.handle;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.example.common.bo.BaseBo;
import org.example.common.bo.GameRound;
import org.example.common.bo.JsonObjectInfo;
import org.example.common.bo.Player;
import org.example.common.constant.CommonConstant;
import org.example.common.enume.GameRoundStatusEnum;
import org.example.common.utils.JSONUtils;

import java.util.Iterator;

public class BoJsonOutboundHandle extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        //System.err.println("接收出站消息：" +  msg);
        if (msg instanceof BaseBo) {
            try {
                String msgJson = getMsgJson(msg);
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

    private String getMsgJson(Object msg) {
        String msgJson = null;
        if (msg instanceof Player) {
            msgJson = simplifyPlayerJson((Player) msg);
        } else {
            msgJson = JSONUtils.toJSONString(msg);
        }
        return msgJson;
    }

    private String simplifyPlayerJson(Player player) {
        GameRound gameRound = player.getGameRound();
        JsonNode jsonNode = JSONUtils.toJsonNode(player);
        JsonNode playerList = jsonNode.get("gameRound").get("playerList");

        //判断是否是明牌
        boolean isNotOpenCards = gameRound.getStatus() == GameRoundStatusEnum.UNDERWAY.getStatus() || gameRound.getPlayerList().size() - gameRound.getFoldPlayerTCount() == 1;

        for (Iterator<JsonNode> it = playerList.elements(); it.hasNext();) {
            Object object = it.next();
            if (object instanceof ObjectNode) {
                ObjectNode playerNode = (ObjectNode) object;
                playerNode.remove("gameRound");
                if (isNotOpenCards) {
                    playerNode.remove("pokers");
                }
            }
        }
        return jsonNode.toString();
    }

}