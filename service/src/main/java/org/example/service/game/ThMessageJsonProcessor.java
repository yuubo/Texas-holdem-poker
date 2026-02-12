package org.example.service.game;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.common.enume.GameRoundStatusEnum;
import org.example.common.handle.MessageJsonProcessor;
import org.example.common.message.BaseBo;
import org.example.common.message.GameRound;
import org.example.common.message.Player;
import org.example.common.utils.JSONUtils;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
public class ThMessageJsonProcessor implements MessageJsonProcessor {

    @Override
    public String process(BaseBo msg) {
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
        boolean isNotOpenCards = gameRound.getStatus() == GameRoundStatusEnum.ACTIVITY.getStatus() || gameRound.getPlayerList().size() - gameRound.getFoldPlayerTCount() == 1;

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
