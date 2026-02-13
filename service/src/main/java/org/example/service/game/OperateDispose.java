package org.example.service.game;

import org.example.common.enume.OperateEnum;
import org.example.common.enume.PlayerStatusEnum;
import org.example.common.message.Operate;
import org.example.common.message.Player;
import org.example.common.utils.SystemMessageUtils;
import org.example.service.channel.PokerChannel;

public class OperateDispose {

    public static Result operate(Player player, PokerChannel channel, GameIndex gameIndex, Operate operate) {
        Result result = new Result();
        call(player, channel, gameIndex, operate, result);
        return result;
    }

    /**
     * 跟注
     */
    private static void call(Player player, PokerChannel channel, GameIndex gameIndex, Operate operate, Result result) {
        //跟注
        if (operate.getOperate() == OperateEnum.CALL.getOperate()) {
            int riseScore = player.getGameRound().getScore() - player.getScore();
            if (player.getScoreTotal() == 0 || player.getScoreTotal() < riseScore) {
                //channel.getChannel().writeAndFlush(SystemMessageUtils.messageSource("service.gameround.hint.b"));
                result.errorCode("service.gameround.hint.b");
                result.isNext(false);
                return;
            }

            player.setStatus(PlayerStatusEnum.CALL.getStatus());
            player.setScore(player.getGameRound().getScore());
            player.setScoreTotal(player.getScoreTotal() - riseScore);
            player.getGameRound().setScoreTotal(player.getGameRound().getScoreTotal() + riseScore);
            result.isNext(true);
            return;
        }

        pass(player, channel, gameIndex, operate, result);
    }

    /**
     * 过牌
     */
    private static void pass(Player player, PokerChannel channel, GameIndex gameIndex, Operate operate, Result result) {
        if (operate.getOperate() == OperateEnum.PASS.getOperate()) {
            if (player.getScore() == player.getGameRound().getScore()) {
                player.setStatus(PlayerStatusEnum.PASS.getStatus());
                result.isNext(true);
                return;
            }
            //channel.getChannel().writeAndFlush(SystemMessageUtils.messageSource("service.gameround.hint.a"));
            result.errorCode("service.gameround.hint.a");
            result.isNext(false);
            return;
        }

        allIn(player, channel, gameIndex, operate, result);
    }

    /**
     * all-in
     */
    private static void allIn(Player player, PokerChannel channel, GameIndex gameIndex, Operate operate, Result result) {
        if (operate.getOperate() == OperateEnum.ALLIN.getOperate()) {
            if (player.getScoreTotal() == 0) {
                //channel.getChannel().writeAndFlush(SystemMessageUtils.messageSource("service.gameround.hint.c"));
                result.errorCode("service.gameround.hint.c");
                result.isNext(false);
                return;
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
            result.isNext(true);
            return;
        }

        fill(player, channel, gameIndex, operate, result);
    }

    /**
     * 加注
     */
    private static void fill(Player player, PokerChannel channel, GameIndex gameIndex, Operate operate, Result result) {
        if (operate.getOperate() == OperateEnum.FILL.getOperate()) {
            //加注
            if (player.getScore() < player.getGameRound().getScore()) {
                operate.setScore(operate.getScore() + player.getGameRound().getScore() - player.getScore());
            } else if (player.getScoreTotal() == 0 && player.getScoreTotal() > (player.getGameRound().getScore() - player.getScore())) {
                //channel.getChannel().writeAndFlush(SystemMessageUtils.messageSource("service.gameround.hint.d"));
                result.errorCode("service.gameround.hint.d");
                result.isNext(false);
                return;
            } else if (player.getScoreTotal() < operate.getScore()){
                //channel.getChannel().writeAndFlush(SystemMessageUtils.messageSource("service.gameround.hint.e"));
                result.errorCode("service.gameround.hint.e");
                result.isNext(false);
                return;
            }

            player.setStatus(PlayerStatusEnum.FILL.getStatus());
            player.setScore(operate.getScore() + player.getScore());
            player.setScoreTotal(player.getScoreTotal() - operate.getScore());
            player.getGameRound().setScoreTotal(player.getGameRound().getScoreTotal() + operate.getScore());
            player.getGameRound().setScore(player.getScore());
            gameIndex.getLastPlayer().player(player).index(gameIndex.getPlayIndex());
            result.isNext(true);
            return;
        }

        fold(player, channel, operate, result);
    }

    /**
     * 弃牌
     */
    private static void fold(Player player, PokerChannel channel, Operate operate, Result result) {
        if (operate.getOperate() == OperateEnum.FOLD.getOperate()) {
            //弃牌
            player.setStatus(PlayerStatusEnum.FOLD.getStatus());
            player.getGameRound().setFoldPlayerTCount(player.getGameRound().getFoldPlayerTCount() + 1);
            result.isNext(true);
            return;
        }
        //channel.getChannel().writeAndFlush(SystemMessageUtils.messageSource("service.gameround.hint.f"));
        result.errorCode("service.gameround.hint.f");
        result.isNext(false);
    }

    public static class Result {
        private boolean isNext;
        private String errorCode;

        public boolean isNext() {
            return isNext;
        }

        public void isNext(boolean isNext) {
            this.isNext = isNext;
        }

        public String errorCode() {
            return errorCode;
        }

        public void errorCode(String errorCode) {
            this.errorCode = errorCode;
        }
    }
}
