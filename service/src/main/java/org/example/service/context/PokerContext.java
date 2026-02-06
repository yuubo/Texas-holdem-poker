package org.example.service.context;

import io.netty.channel.EventLoop;
import org.example.common.bo.*;
import org.example.common.channel.PokerChannel;
import org.example.common.enume.GameRoundStatusEnum;
import org.example.common.enume.OperateEnum;
import org.example.common.enume.PlayerActivityEnum;
import org.example.common.enume.PlayerStatusEnum;
import org.example.common.utils.SystemMessageUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class PokerContext {

    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final int SCALE = 2;

    private List<Poker> pokerList = new LinkedList<>();

    private List<PokerChannel> pokerChannelList = null;

    private Map<User, Player> playerMap = new HashMap<>();

    //牌局,每局结束时+1 不清零
    private int partyIndex = 0;

    //每轮最后一个加注玩家 每局清零
    private int fillLastPlayIndex = 0;

    //玩家操作索引，每局清零
    private int playIndex = 0;

    private GameRound gameRound = null;

    public PokerContext() {
        ApplicationContextGatherUtils.pokerContext = this;
    }

    public void start() {
        initialize();
        PokerUtils.createPoker(pokerList);
        initializePlayer();
        //informHandle(excludeChannel, null);
        next();
    }

    private void initialize() {
        playIndex = 0;

        pokerChannelList = ApplicationContextGatherUtils.nettyApplicationContext().getPokerChannel();
        if (gameRound ==  null) {
            gameRound = new GameRound();
            gameRound.setId(gameRound.hashCode());
            gameRound.setCommonPokerList(new ArrayList<>());
            gameRound.setPlayerList(new ArrayList<>());
        }
        gameRound.setScoreTotal(15);
        gameRound.setScore(10);
        gameRound.setStatus(GameRoundStatusEnum.UNDERWAY.getStatus());
        gameRound.setFoldPlayerTCount(0);

        pokerList.clear();
    }

    private  void initializePlayer() {
        int blindIndex = partyIndex % pokerChannelList.size();
        for (int i = 0; i < pokerChannelList.size(); i++) {
            PokerChannel pokerChannel = pokerChannelList.get(i);

            Player player = null;
            if (playerMap.containsKey(pokerChannel.getUser())) {
                player = playerMap.get(pokerChannel.getUser());
                if (player.getScoreTotal() < 10) {
                    player.setStatus(PlayerStatusEnum.ONLOOKER.getStatus());
                }

                player.setGrade(0);
                player.setActivity(PlayerActivityEnum.NORMAL.getNumber());
                player.setStatus(PlayerStatusEnum.NORMAL.getStatus());
                player.setPartyWinScore(0);
            } else {
                player = createPlayer(pokerChannel.getUser());
            }

            if (i == blindIndex) {
                //大盲注
                player.setScore(10);
                player.setScoreTotal(player.getScoreTotal() - player.getScore());
                player.setStatus(PlayerStatusEnum.BIG_BLIND.getStatus());
                fillLastPlayIndex = i;
            }  else if (i == blindIndex + 1) {
                //小盲注
                playIndex = i;
                player.setScore(5);
                player.setScoreTotal(player.getScoreTotal() - player.getScore());
                player.setStatus(PlayerStatusEnum.SMALL_BLIND.getStatus());
            } else {
                player.setScore(0);
                player.setStatus(PlayerStatusEnum.NORMAL.getStatus());
            }

        }
    }

    private Player createPlayer(User user) {
        Player player = new Player();
        player.setId(player.hashCode() + user.hashCode());
        player.setPokers(PokerUtils.getPoker(2, pokerList));
        player.setUser(user);
        player.setGameRound(gameRound);
        player.setScoreTotal(1000);
        gameRound.getPlayerList().add(player);
        playerMap.put(user, player);
        return player;
    }

    public void operateHandle(Operate operate, PokerChannel channel) {
        if (getPokerChannelByPlayerIndex() != channel) {
            return;
        }

        if (operate.getOperate() == OperateEnum.START.getOperate()) {
            start();
            return;
        }
        Player player = playerMap.get(channel.getUser());
        player.setActivity(PlayerActivityEnum.NORMAL.getNumber());
        if (operate.getOperate() == OperateEnum.PASS.getOperate()) {
            pass(player, channel);
        } else if (operate.getOperate() == OperateEnum.CALL.getOperate()) {
            //跟注
            call(player, channel);
        } else if (operate.getOperate() == OperateEnum.ALLIN.getOperate()) {
            //all-in
            allIn(player, channel);
        } else if (operate.getOperate() == OperateEnum.FILL.getOperate()) {
            //加注
            fill(player, channel, operate);
        } else if (operate.getOperate() == OperateEnum.FOLD.getOperate()) {
            //弃牌
            fold(player, channel);
        }
    }

    public void call(Player player, PokerChannel channel) {
        //跟注
        int riseScore = gameRound.getScore() - player.getScore();
        if (player.getScoreTotal() == 0 || player.getScoreTotal() < riseScore) {
            channel.getChannel().writeAndFlush(SystemMessageUtils.messageSource("service.gameround.hint.b"));
        } else {
            player.setStatus(PlayerStatusEnum.CALL.getStatus());
            player.setScore(gameRound.getScore());
            player.setScoreTotal(player.getScoreTotal() - riseScore);
            gameRound.setScoreTotal(gameRound.getScoreTotal() + riseScore);
            playIndex ++;
            next();
        }
    }

    public void pass(Player player, PokerChannel channel) {
        if (player.getScore() == gameRound.getScore()) {
            player.setStatus(PlayerStatusEnum.PASS.getStatus());
            playIndex ++;
            next();
        } else {
            channel.getChannel().writeAndFlush(SystemMessageUtils.messageSource("service.gameround.hint.a"));
        }
    }

    public void allIn(Player player, PokerChannel channel) {
        if (player.getScoreTotal() == 0) {
            channel.getChannel().writeAndFlush(SystemMessageUtils.messageSource("service.gameround.hint.c"));
        } else {
            player.setStatus(PlayerStatusEnum.ALL_IN.getStatus());
            if (player.getScoreTotal() > gameRound.getScore()) {
                gameRound.setScoreTotal(gameRound.getScoreTotal() + player.getScoreTotal());
                gameRound.setScore(player.getScoreTotal());
                player.setScore(player.getScore() + player.getScoreTotal());
                player.setScoreTotal(0);
                fillLastPlayIndex = playIndex;
            } else {
                gameRound.setScoreTotal(gameRound.getScoreTotal() + player.getScoreTotal());
                player.setScore(player.getScore() + player.getScoreTotal());
                player.setScoreTotal(0);
            }
            player.setStatus(PlayerStatusEnum.ALL_IN.getStatus());
            playIndex ++;
            next();
        }
    }

    public void fill(Player player, PokerChannel channel, Operate operate) {
        //加注
        if (player.getScoreTotal() == 0 && player.getScoreTotal() > (gameRound.getScore() - player.getScore())) {
            channel.getChannel().writeAndFlush(SystemMessageUtils.messageSource("service.gameround.hint.d"));
        } else if (player.getScoreTotal() < operate.getScore()){
            channel.getChannel().writeAndFlush(SystemMessageUtils.messageSource("service.gameround.hint.e"));
        } else {
            clearFillStatus(player);
            player.setStatus(PlayerStatusEnum.FILL.getStatus());
            player.setScore(operate.getScore() + player.getScore());
            player.setScoreTotal(player.getScoreTotal() - operate.getScore());
            gameRound.setScoreTotal(gameRound.getScoreTotal() + operate.getScore());
            gameRound.setScore(player.getScore());
            fillLastPlayIndex = playIndex;
            playIndex ++;
            next();
        }
    }

    public void fold(Player player, PokerChannel channel) {
        //弃牌
        player.setStatus(PlayerStatusEnum.FOLD.getStatus());
        gameRound.setFoldPlayerTCount(gameRound.getFoldPlayerTCount() + 1);
        playIndex ++;
        next();
    }

    /**
     * 清除其他玩家的加注状态
     * @param player
     */
    private void clearFillStatus(Player player) {
        playerMap.forEach((user, pl) -> {
            if (pl != player && pl.getStatus() == PlayerStatusEnum.FILL.getStatus()) {
                pl.setStatus(PlayerStatusEnum.NORMAL.getStatus());
            }
        });
    }

    private PokerChannel getPokerChannelByPlayerIndex() {
        return pokerChannelList.get(playIndex % pokerChannelList.size());
    }

    public void next() {
        if (playerMap.size() - 1 == gameRound.getFoldPlayerTCount()) {
            //弃牌只剩一个玩家时结束本局
            finish();
            return;
        }
        PokerChannel pokerChannel = getPokerChannelByPlayerIndex();
        User user = pokerChannel.getUser();
        Player pl = playerMap.get(user);
        if (pl.getStatus() == PlayerStatusEnum.FOLD.getStatus() || pl.getStatus() == PlayerStatusEnum.ONLOOKER.getStatus()) {
            playIndex ++;
            next();
            return;
        }

        if (playIndex != fillLastPlayIndex && playIndex - playerMap.size() == fillLastPlayIndex) {
            if (pl.getScore() == gameRound.getScore()) {
                pl.setStatus(PlayerStatusEnum.NORMAL.getStatus());
                if (gameRound.getCommonPokerList().size() == 5) {
                    finish();
                } else {
                    pl.setActivity(PlayerActivityEnum.ACTIVITY.getNumber());
                    sendCommonPoker();
                }
                return;
            }
        }

        Operate operate = new Operate();
        if (pl.getScoreTotal() == 0) {
            //没有筹码时直接过牌
            operate.setOperate(OperateEnum.PASS.getOperate());
            operateHandle(operate, pokerChannel);
        } else {
            //SystemMessageUtils.stringMessage("等待" + user.getName() + "下注", pokerChannelList.get(playIndex));
            operate.setAllowOperates(new ArrayList<>());
            if (pl.getScore() == gameRound.getScore()) {
                operate.getAllowOperates().add(OperateEnum.PASS.getOperate());
            }
            if (pl.getScoreTotal() < gameRound.getScore()) {
                operate.getAllowOperates().add(OperateEnum.ALLIN.getOperate());
            }
            if (pl.getScoreTotal() >= (gameRound.getScore() - pl.getScore())) {
                operate.getAllowOperates().add(OperateEnum.CALL.getOperate());
                operate.getAllowOperates().add(OperateEnum.FILL.getOperate());
            }

            operate.getAllowOperates().add(OperateEnum.FOLD.getOperate());

            pl.setActivity(PlayerActivityEnum.ACTIVITY.getNumber());
            pl.setStatus(PlayerStatusEnum.NORMAL.getStatus());

            informHandle(null, pokerChannel.getChannel().eventLoop());

            pokerChannel.getChannel().eventLoop().execute(() -> {
                pokerChannel.getChannel().writeAndFlush(operate);
            });
        }
    }

    private void sendCommonPoker() {
        fillLastPlayIndex = playIndex;
        int limit = 1;
        if (gameRound.getCommonPokerList().isEmpty()) {
            limit = 3;
        }

        gameRound.getCommonPokerList().addAll(PokerUtils.getPoker(limit, pokerList));
        pokerChannelList.forEach(pokerChannel -> {
            Player player = playerMap.get(pokerChannel.getUser());
            if (player.getStatus() == PlayerStatusEnum.BIG_BLIND.getStatus()
                    || player.getStatus() == PlayerStatusEnum.SMALL_BLIND.getStatus()) {
                player.setStatus(PlayerStatusEnum.NORMAL.getStatus());
            }
            pokerChannel.getChannel().writeAndFlush(player);
        });

        next();
    }

    public void finish() {
        partyIndex ++;
        gameRound.setStatus(GameRoundStatusEnum.FINISH.getStatus());
        gameRound.setCalculateScoreTotal(BigDecimal.valueOf(gameRound.getScoreTotal()));
        gameRound.setCalculateScore(BigDecimal.valueOf(gameRound.getScore()));

        List<Map.Entry<Integer, List<Player>>> gradeList = grade();
        ruling(gradeList);
        informHandle(null, pokerChannelList.getFirst().getChannel().eventLoop());
    }

    /**
     * 牌等级大小排序
     * @return
     */
    private List<Map.Entry<Integer, List<Player>>> grade() {
        Map<Integer, List<Player>> lastPlayerMap = new HashMap<>();
        for (Player player : playerMap.values()) {
            if (player.getStatus() == PlayerStatusEnum.FOLD.getStatus()) {
                continue;
            }
            List<Poker> pokers = new ArrayList<>();
            pokers.addAll(player.getPokers());
            pokers.addAll(gameRound.getCommonPokerList());
            //player.setGrade(PokerUtils.grade(pokers, player));
            PokerUtils.grade(pokers, player);
            player.setCalculateScore(BigDecimal.valueOf(player.getScore()));
            player.setCalculateTotalScore(BigDecimal.valueOf(player.getScoreTotal()));
            player.setCalculatePartyWinScore(BigDecimal.ZERO);

            List<Player> list = null;
            if (lastPlayerMap.containsKey(player.getGrade())) {
                list = lastPlayerMap.get(player.getGrade());
            } else {
                list = new ArrayList<>();
                lastPlayerMap.put(player.getGrade(), list);
            }
            list.add(player);
        }
        List<Map.Entry<Integer, List<Player>>> list = new ArrayList<>(lastPlayerMap.entrySet());
        Collections.sort(list, (o1, o2) -> o2.getKey() - o1.getKey());

        return list;
    }

    /**
     * 赢家分账
     * @param gradeList
     */
    private void ruling(List<Map.Entry<Integer, List<Player>>> gradeList) {
        Map<BigDecimal, Map<Player, BigDecimal>> proportionMap = new HashMap<>();

        for (int i = 0; i < gradeList.size(); i++) {

            Map.Entry<Integer, List<Player>> gradePlayerEntry = gradeList.get(i);


            for (int j = 0; j < gradePlayerEntry.getValue().size(); j++) {

                Player winPlayer = gradePlayerEntry.getValue().get(j);

                BigDecimal maxScore = maxWinScore(gradePlayerEntry.getValue(), gradePlayerEntry.getKey());

                for (Player pl : playerMap.values()) {
                    if (pl == winPlayer ){
                        //赢家自己的下注筹码直接返还
                        gameRound.setCalculateScoreTotal(gameRound.getCalculateScoreTotal().subtract(winPlayer.getCalculateScore()));
                        winPlayer.getCalculatePartyWinScore().add(winPlayer.getCalculateScore());
                    } else {
                        //筹码扣完或pl分数比当前赢家高时跳过处理
                        if (pl.getCalculateScore().compareTo(BigDecimal.ONE) < 1 || pl.getGrade() >= winPlayer.getGrade()) {
                            continue;
                        }

                        BigDecimal score = pl.getCalculateScore();
                        if (pl.getCalculateScore().compareTo(maxScore) > 0) {
                            /*输家的下注高于赢家时，输家的筹码按照赢家的筹码一样处理，玩家最多只能赢自己下注的100%筹码
                             *比如A和B玩家都ALLIN，A有60筹码，B有100筹码，A只能赢60筹码，相当于B只下了60筹码
                             */
                            score = maxScore;
                        }
                        if (!proportionMap.containsKey(pl.getCalculateScore()) || !proportionMap.get(pl.getCalculateScore()).containsKey(pl)) {
                            proportionCalculate(gradePlayerEntry.getValue(), pl.getCalculateScore(), proportionMap);
                        }

                        BigDecimal proportion = proportionMap.get(pl.getCalculateScore()).get(pl);
                        //赢家取走的筹码
                        BigDecimal takenAway = score.multiply(proportion);

                        winPlayer.getCalculateTotalScore().add(takenAway);
                        winPlayer.getCalculatePartyWinScore().add(takenAway);
                        gameRound.getCalculateScoreTotal().subtract(takenAway);
                        pl.getCalculateScore().subtract(takenAway);
                    }

                    if (gameRound.getCalculateScoreTotal().compareTo(BigDecimal.ONE) < 1) {
                        return;
                    }
                }
            }
        }
    }

    /**
     * 得出合适的筹码，赢家的注码不应该高于输家的最高注码
     * 赢家出100筹码，输家只有90筹码， 赢家最高只能赢得90筹码
     * @param winPlayerList
     * @param grade
     * @return
     */
    private BigDecimal maxWinScore(List<Player> winPlayerList, int grade) {
        BigDecimal maxWinScore = winPlayerList.stream().map(Player::getCalculateScore).max(BigDecimal::compareTo).get();
        /*BigDecimal maxLoserScore = maxLoserScore(grade);
        BigDecimal score = maxWinScore;
        if (maxWinScore.compareTo(maxLoserScore) > 0) {
            score = maxLoserScore;
        }
        return score;
        */
        return maxWinScore;
    }

    private Map<Player, BigDecimal> proportion(List<Player> winPlayerList, BigDecimal maxScore) {
        BigDecimal total = BigDecimal.ZERO;
        winPlayerList.stream().forEach(player -> {
            total.add(player.getCalculateScore());
        });

        Map<Player, BigDecimal> map = new HashMap<>();
        for (Player player : winPlayerList) {
            if (player.getCalculateScore().compareTo(maxScore) > 0) {
                player.setExcessiveScore(player.getCalculateScore().subtract(maxScore));
                player.setCalculateScore(maxScore);
            }

            BigDecimal proportion = player.getCalculateScore().divide(total, SCALE, ROUNDING_MODE);

            map.put(player, proportion);
        }

        return map;
    }

    private void proportionCalculate(List<Player> winPlayerList, BigDecimal loserPlayScore,
                             Map<BigDecimal, Map<Player, BigDecimal>> proportionMap) {

        BigDecimal total = BigDecimal.ZERO;
        winPlayerList.stream().forEach(player -> {
            if (player.getCalculateScore().compareTo(loserPlayScore) > 0) {
                /*赢家的下注筹码比输家多时，实际下注筹码只能等于输家的下注筹码
                 *比如A和B玩家都ALLIN，A有60筹码，B有100筹码，B赢也只能赢60筹码，相当于B也下注60
                 */
                total.add(loserPlayScore);
            } else {
                total.add(player.getCalculateScore());
            }
        });

        Map<Player, BigDecimal> map = null;
        for (Player player : winPlayerList) {
            map = proportionMap.get(loserPlayScore);
            if (map == null) {
                map = new HashMap<>();
                proportionMap.put(loserPlayScore, map);
            }

            BigDecimal proportion = player.getCalculateScore().divide(total, SCALE, ROUNDING_MODE);

            map.put(player, proportion);
        }

    }

    public void informHandle(PokerChannel excludeChannel, EventLoop eventLoop) {
        if (eventLoop == null) {
            eventLoop = excludeChannel.getChannel().eventLoop();
        }

        eventLoop.execute(() -> {
            pokerChannelList.forEach(pokerChannel -> {
                if (excludeChannel == null || pokerChannel != excludeChannel) {
                    pokerChannel.getChannel().writeAndFlush(playerMap.get(pokerChannel.getUser()));
                }
            });
        });

    }

    public GameRound getGameRound() {
        return gameRound;
    }

}
