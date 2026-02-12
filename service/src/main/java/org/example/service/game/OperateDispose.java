package org.example.service.game;

import org.example.common.enume.OperateEnum;
import org.example.common.enume.PlayerStatusEnum;
import org.example.common.message.Operate;
import org.example.common.message.Player;
import org.example.common.utils.SystemMessageUtils;
import org.example.service.channel.PokerChannel;

public class OperateDispose {

    public static boolean operate(Player player, PokerChannel channel, GameIndex gameIndex, Operate operate) {
        return call(player, channel, gameIndex, operate);
    }

    /**
     * 跟注
     */
    private static boolean call(Player player, PokerChannel channel, GameIndex gameIndex, Operate operate) {
        //跟注
        if (operate.getOperate() == OperateEnum.CALL.getOperate()) {
            int riseScore = player.getGameRound().getScore() - player.getScore();
            if (player.getScoreTotal() == 0 || player.getScoreTotal() < riseScore) {
                channel.getChannel().writeAndFlush(SystemMessageUtils.messageSource("service.gameround.hint.b"));
                return false;
            }

            player.setStatus(PlayerStatusEnum.CALL.getStatus());
            player.setScore(player.getGameRound().getScore());
            player.setScoreTotal(player.getScoreTotal() - riseScore);
            player.getGameRound().setScoreTotal(player.getGameRound().getScoreTotal() + riseScore);
            return true;
        }
        return pass(player, channel, gameIndex, operate);
    }

    /**
     * 过牌
     */
    private static boolean pass(Player player, PokerChannel channel, GameIndex gameIndex, Operate operate) {
        if (operate.getOperate() == OperateEnum.PASS.getOperate()) {
            if (player.getScore() == player.getGameRound().getScore()) {
                player.setStatus(PlayerStatusEnum.PASS.getStatus());
                return true;
            }
            channel.getChannel().writeAndFlush(SystemMessageUtils.messageSource("service.gameround.hint.a"));
            return false;
        }
        return allIn(player, channel, gameIndex, operate);
    }

    /**
     * all-in
     */
    private static boolean allIn(Player player, PokerChannel channel, GameIndex gameIndex, Operate operate) {
        if (operate.getOperate() == OperateEnum.ALLIN.getOperate()) {
            if (player.getScoreTotal() == 0) {
                channel.getChannel().writeAndFlush(SystemMessageUtils.messageSource("service.gameround.hint.c"));
                return false;
            }
            player.setStatus(PlayerStatusEnum.ALL_IN.getStatus());
            if (player.getScoreTotal() > player.getGameRound().getScore()) {
                player.getGameRound().setScoreTotal(player.getGameRound().getScoreTotal() + player.getScoreTotal());
                player.getGameRound().setScore(player.getScoreTotal());
                player.setScore(player.getScore() + player.getScoreTotal());
                player.setScoreTotal(0);
                gameIndex.getLastPlayer().player(player).index(gameIndex.getPlayIndex());
            } else {
                player.getGameRound().setScoreTotal(player.getGameRound().getScoreTotal() + player.getScoreTotal());
                player.setScore(player.getScore() + player.getScoreTotal());
                player.setScoreTotal(0);
            }
            player.setStatus(PlayerStatusEnum.ALL_IN.getStatus());
            return true;
        }
        return fill(player, channel, gameIndex, operate);
    }

    /**
     * 加注
     */
    private static boolean fill(Player player, PokerChannel channel, GameIndex gameIndex, Operate operate) {
        if (operate.getOperate() == OperateEnum.FILL.getOperate()) {
            //加注
            if (player.getScoreTotal() == 0 && player.getScoreTotal() > (player.getGameRound().getScore() - player.getScore())) {
                channel.getChannel().writeAndFlush(SystemMessageUtils.messageSource("service.gameround.hint.d"));
                return false;
            } else if (player.getScoreTotal() < operate.getScore()){
                channel.getChannel().writeAndFlush(SystemMessageUtils.messageSource("service.gameround.hint.e"));
                return false;
            }
            clearFillStatus(player);
            player.setStatus(PlayerStatusEnum.FILL.getStatus());
            player.setScore(operate.getScore() + player.getScore());
            player.setScoreTotal(player.getScoreTotal() - operate.getScore());
            player.getGameRound().setScoreTotal(player.getGameRound().getScoreTotal() + operate.getScore());
            player.getGameRound().setScore(player.getScore());
            gameIndex.getLastPlayer().player(player).index(gameIndex.getPlayIndex());
            return true;
        }
        return fold(player, channel, operate);
    }

    /**
     * 清除其他玩家的加注状态
     */
    private static void clearFillStatus(Player player) {
        player.getGameRound().getPlayerList().forEach((pl) -> {
            if (pl != player
                    && pl.getStatus() == PlayerStatusEnum.FILL.getStatus()
                    && pl.getStatus() != PlayerStatusEnum.ONLOOKER.getStatus()) {
                pl.setStatus(PlayerStatusEnum.NORMAL.getStatus());
            }
        });
    }

    /**
     * 弃牌
     */
    private static boolean fold(Player player, PokerChannel channel, Operate operate) {
        if (operate.getOperate() == OperateEnum.FOLD.getOperate()) {
            //弃牌
            player.setStatus(PlayerStatusEnum.FOLD.getStatus());
            player.getGameRound().setFoldPlayerTCount(player.getGameRound().getFoldPlayerTCount() + 1);
            return true;
        }
        channel.getChannel().writeAndFlush(SystemMessageUtils.messageSource("service.gameround.hint.f"));
        return false;
    }
}
