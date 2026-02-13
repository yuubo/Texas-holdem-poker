package org.example.service.game;

import io.netty.channel.EventLoop;
import org.example.common.enume.GameRoundStatusEnum;
import org.example.common.enume.OperateEnum;
import org.example.common.enume.PlayerActivityEnum;
import org.example.common.enume.PlayerStatusEnum;
import org.example.common.message.*;
import org.example.common.utils.SystemMessageUtils;
import org.example.service.channel.PokerChannel;
import org.example.service.channel.PokerChannelStatusEnum;
import org.example.service.utils.PokerUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

public class PokerRoom {

    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final int SCALE = 2;

    private final List<Poker> pokerList = new LinkedList<>();

    private final List<PokerChannel> pokerChannelList = new ArrayList<>();

    private final Map<User, Player> playerMap = new HashMap<>();

    private final GameIndex gameIndex = new GameIndex();

    private final GameRound gameRound;

    private final EventLoop eventLoop;

    private final int roomId;

    private final ReentrantLock lock = new ReentrantLock();

    private PokerChannel nowOperate;

    public PokerRoom(EventLoop eventLoop, int roomId) {
        this.roomId = roomId;
        this.eventLoop = eventLoop;
        this.gameRound = new GameRound();
        gameRound.setId(gameRound.hashCode());
        gameRound.setCommonPokerList(new ArrayList<>());
        gameRound.setPlayerList(new ArrayList<>());
    }

    public void addPlayer(PokerChannel pokerChannel) {
        lock.lock();
        try {
            pokerChannelList.add(pokerChannel);
            createPlayer(pokerChannel.getUser());
            System.out.println(pokerChannel.getUser().getName() + "加入房间" + roomId);
            String name = pokerChannel.getUser().getName();
            informPlayer(p -> {
                Message s = SystemMessageUtils.messageSource("service.gameround.hint.g", new Object[]{name, roomId});
                return s;
            }, null);
        } finally {
            lock.unlock();
        }
    }

    public void start() {
        lock.lock();
        try {
            if (gameRound.getStatus() == GameRoundStatusEnum.ACTIVITY.getStatus()) {
                return;
            }

            initialize();
            PokerUtils.createPoker(pokerList);
            initializePlayer();
            next();
        } finally {
            lock.unlock();
        }
    }

    private void initialize() {
        pokerList.clear();
        Iterator<PokerChannel> iterator = pokerChannelList.iterator();
        while (iterator.hasNext()) {
            PokerChannel pokerChannel = iterator.next();
            if (pokerChannel.getStatus() == PokerChannelStatusEnum.DISCONNECT.getStatus()) {
                playerMap.remove(pokerChannel.getUser());
                iterator.remove();
            }
        }
        gameIndex.setPlayIndex(0);
        gameIndex.setPartyPlayerCount(pokerChannelList.size());
        gameRound.setScoreTotal(15);
        gameRound.setScore(10);
        gameRound.setStatus(GameRoundStatusEnum.ACTIVITY.getStatus());
        gameRound.setFoldPlayerTCount(0);
        gameRound.getCommonPokerList().clear();
    }

    private  void initializePlayer() {
        //盲注位置
        int blindIndex = gameIndex.getPartyIndex() % gameIndex.getPartyPlayerCount();

        for (int i = 0; i < gameIndex.getPartyPlayerCount(); i++) {
            PokerChannel pokerChannel = pokerChannelList.get(i);

            Player player = playerMap.get(pokerChannel.getUser());
            if (player.getScoreTotal() < 10) {
                player.setStatus(PlayerStatusEnum.ONLOOKER.getStatus());
            }

            player.setGrade(0);
            player.setActivity(PlayerActivityEnum.NORMAL.getNumber());
            player.setStatus(PlayerStatusEnum.NORMAL.getStatus());
            player.setPartyWinScore(0);
            player.setPokers(PokerUtils.getPoker(2, pokerList));
            player.setWinPokers(null);

            if (i == blindIndex) {
                //大盲注
                player.setScore(10);
                player.setScoreTotal(player.getScoreTotal() - player.getScore());
                player.setStatus(PlayerStatusEnum.BIG_BLIND.getStatus());
            }  else if (i == blindIndex + 1) {
                //小盲注
                gameIndex.getLastPlayer().player(player).index(i);
                gameIndex.setPlayIndex(i);
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
        player.setUser(user);
        player.setGameRound(gameRound);
        player.setScoreTotal(1000);
        if (gameRound.getStatus() == GameRoundStatusEnum.ACTIVITY.getStatus()) {
            player.setStatus(PlayerStatusEnum.ONLOOKER.getStatus());
        }
        gameRound.getPlayerList().add(player);
        playerMap.put(user, player);
        return player;
    }

    private PokerChannel getPokerChannelByPlayerIndex(int index) {
        return pokerChannelList.get(index % gameIndex.getPartyPlayerCount());
    }

    public void operateHandle(Operate operate, PokerChannel channel) {
        if (operate.getOperate() == OperateEnum.START.getOperate()) {
            start();
            return;
        }

        if (getPokerChannelByPlayerIndex(gameIndex.getPlayIndex()) != channel || nowOperate == channel) {
            //判断是否是当前待操作玩家，或者当前玩家重复操作，则不处理
            return;
        }

        nowOperate = channel;
        Player player = playerMap.get(channel.getUser());
        player.setOperate(null);
        player.setActivity(PlayerActivityEnum.NORMAL.getNumber());

        //处理操作
        OperateDispose.Result result = OperateDispose.operate(player, channel, gameIndex, operate);

        if (result.isNext()) {
            if (gameIndex.getPartyPlayerCount() - 1 == gameRound.getFoldPlayerTCount()) {
                //弃牌只剩一个玩家时结束本局
                gameRound.setOnlyOne(true);
                finish();
                return;
            }

            PokerChannel pc = getPokerChannelByPlayerIndex(gameIndex.getPlayIndex() + 1);
            Player pl = playerMap.get(pc.getUser());
            if (pl == gameIndex.getLastPlayer().player() && gameRound.getScore() == pl.getScore()) {
                player.setStatus(PlayerStatusEnum.NORMAL.getStatus());
                if (gameRound.getCommonPokerList().size() == 5) {
                    finish();
                } else {
                    gameIndex.playIndexAdd();
                    gameIndex.getLastPlayer().index(gameIndex.getPlayIndex());
                    sendCommonPoker();
                }
                return;
            }

            gameIndex.playIndexAdd();
            next();
        } else {
            nowOperate = null;
            channel.getChannel().writeAndFlush(SystemMessageUtils.messageSource(result.errorCode()));
        }
    }

    private void next() {
        nowOperate = null;
        PokerChannel pokerChannel = getPokerChannelByPlayerIndex(gameIndex.getPlayIndex());
        User user = pokerChannel.getUser();
        System.out.println(user.getName() + "操作" + gameIndex.getPlayIndex());
        Player pl = playerMap.get(user);
        if (pl.getStatus() == PlayerStatusEnum.ONLOOKER.getStatus()) {
            next();
            return;
        }
        if (pl.getStatus() == PlayerStatusEnum.FOLD.getStatus()) {
            gameIndex.playIndexAdd();
            next();
            return;
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
            if (pl.getScoreTotal() >= (gameRound.getScore() - pl.getScore())) {
                if (pl.getScore() < gameRound.getScore()) {
                    operate.getAllowOperates().add(OperateEnum.CALL.getOperate());
                }
                operate.getAllowOperates().add(OperateEnum.FILL.getOperate());
            }
            if (pl.getScoreTotal() > gameRound.getScore()) {
                operate.getAllowOperates().add(OperateEnum.ALLIN.getOperate());
            }

            operate.getAllowOperates().add(OperateEnum.FOLD.getOperate());

            pl.setActivity(PlayerActivityEnum.ACTIVITY.getNumber());
            pl.setStatus(PlayerStatusEnum.NORMAL.getStatus());
            pl.setOperate(operate);

            refreshPlayer();

        }
    }

    private void sendCommonPoker() {
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
            /*if (pokerChannel.getStatus() != PokerChannelStatusEnum.DISCONNECT.getStatus()) {
                pokerChannel.getChannel().writeAndFlush(player);
            }*/
        });

        //refreshPlayer();

        next();
    }

    public void finish() {
        gameIndex.partyIndexAdd();
        gameRound.setStatus(GameRoundStatusEnum.FINISH.getStatus());
        gameRound.setCalculateScoreTotal(BigDecimal.valueOf(gameRound.getScoreTotal()));
        gameRound.setCalculateScore(BigDecimal.valueOf(gameRound.getScore()));

        List<Map.Entry<Integer, List<Player>>> gradeList = grade();
        //给赢家分配筹码
        ruling(gradeList);

        //玩家得分类型转换
        transitionScoreType();

        refreshPlayer();
    }

    /**
     * 玩家得分
     * @return
     */
    private List<Map.Entry<Integer, List<Player>>> grade() {
        Map<Integer, List<Player>> lastPlayerMap = new HashMap<>();
        List<Poker> pokers = new ArrayList<>(7);
        pokers.addAll(gameRound.getCommonPokerList());
        for (Player player : playerMap.values()) {
            if (player.getStatus() == PlayerStatusEnum.ONLOOKER.getStatus()) {
                continue;
            }

            if (player.getStatus() == PlayerStatusEnum.FOLD.getStatus()) {
                player.setGrade(0);
            } else {
                if (gameRound.isOnlyOne()) {
                    player.setGrade(10000);
                } else {
                    pokers.add(5, player.getPokers().get(0));
                    pokers.add(6, player.getPokers().get(1));
                    PokerUtils.grade(pokers, player);
                }
            }
            player.setCalculateScore(BigDecimal.valueOf(player.getScore()));
            player.setCalculateTotalScore(BigDecimal.valueOf(player.getScoreTotal()));
            player.setCalculatePartyWinScore(BigDecimal.valueOf(0));
            player.setExcessiveScore(BigDecimal.valueOf(0));

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
        //赢家分账比例 key:输家下注 value: k玩家--v分账比例
        Map<String, Map<Player, BigDecimal>> proportionMap = new HashMap<>();

        for (int i = 0; i < gradeList.size(); i++) {

            Map.Entry<Integer, List<Player>> gradePlayerEntry = gradeList.get(i);
            for (int j = 0; j < gradePlayerEntry.getValue().size(); j++) {

                Player winPlayer = gradePlayerEntry.getValue().get(j);
                BigDecimal maxScore = maxWinScore(gradePlayerEntry.getValue(), gradePlayerEntry.getKey());

                for (Player pl : playerMap.values()) {
                    if (pl == winPlayer ){
                        //赢家自己的下注筹码直接返还
                        gameRound.setCalculateScoreTotal(gameRound.getCalculateScoreTotal().subtract(winPlayer.getCalculateScore()));
                        winPlayer.setCalculatePartyWinScore(winPlayer.getCalculatePartyWinScore().add(winPlayer.getCalculateScore()));
                        winPlayer.setCalculateTotalScore(winPlayer.getCalculateTotalScore().add(winPlayer.getCalculateScore()));
                    } else {
                        //筹码扣完、观众、pl分数比当前赢家高时跳过处理
                        if (pl.getStatus() == PlayerStatusEnum.ONLOOKER.getStatus()
                                || pl.getCalculateScore().compareTo(BigDecimal.ONE) < 1
                                || pl.getGrade() >= winPlayer.getGrade()) {

                            continue;
                        }

                        BigDecimal score = pl.getCalculateScore();
                        if (pl.getCalculateScore().compareTo(maxScore) > 0) {
                            /*输家的下注高于赢家时，输家的筹码按照赢家的筹码一样处理，玩家最多只能赢自己下注的100%筹码
                             *比如A和B玩家都ALLIN，A有60筹码，B有100筹码，A只能赢60筹码，相当于B只下了60筹码
                             */
                            score = maxScore;
                        }

                        if (!proportionMap.containsKey(pl.getCalculateScore().toPlainString())
                                || !proportionMap.get(pl.getCalculateScore().toPlainString()).containsKey(winPlayer)) {

                            proportionCalculate(gradePlayerEntry.getValue(), pl.getCalculateScore(), proportionMap);
                        }

                        String key = pl.getCalculateScore().toPlainString();
                        BigDecimal proportion = proportionMap.get(key).get(winPlayer);
                        //赢家取走的筹码
                        BigDecimal takenAway = score.multiply(proportion);

                        winPlayer.setCalculateTotalScore(winPlayer.getCalculateTotalScore().add(takenAway));
                        winPlayer.setCalculatePartyWinScore(winPlayer.getCalculatePartyWinScore().add(takenAway));
                        gameRound.setCalculateScoreTotal(gameRound.getCalculateScoreTotal().subtract(takenAway));
                        pl.setCalculateScore(pl.getCalculateScore().subtract(takenAway));
                    }

                    if (gameRound.getCalculateScoreTotal().compareTo(BigDecimal.ONE) < 1) {
                        return;
                    }
                }
            }
        }
    }

    /**
     * 玩家得分转换
     */
    private void transitionScoreType() {
        playerMap.values().forEach((pl) -> {
            pl.setScoreTotal(pl.getCalculateTotalScore().intValue());
            pl.setPartyWinScore(pl.getCalculatePartyWinScore().intValue());
            if (pl.getPartyWinScore() == 0 && pl.getCalculateScore().compareTo(BigDecimal.ONE) >= 0) {
                pl.setScoreTotal(pl.getScoreTotal() + pl.getCalculateScore().intValue());
            }
        });
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
        return maxWinScore;
    }

    /**
     * 玩家分账比例计算
     */
    private void proportionCalculate(List<Player> winPlayerList, BigDecimal loserPlayScore,
                             Map<String, Map<Player, BigDecimal>> proportionMap) {

        BigDecimal total = BigDecimal.valueOf(0);
        for (Player player : winPlayerList) {
            if (player.getCalculateScore().compareTo(loserPlayScore) > 0) {
                /*赢家的下注筹码比输家多时，实际下注筹码只能等于输家的下注筹码
                 *比如A和B玩家都ALLIN，A有60筹码，B有100筹码，B赢也只能赢60筹码，相当于B也下注60
                 */
                total = total.add(loserPlayScore);
            } else {
                total = total.add(player.getCalculateScore());
            }
        }

        Map<Player, BigDecimal> map = null;
        for (Player player : winPlayerList) {
            map = proportionMap.get(loserPlayScore);
            if (map == null) {
                map = new HashMap<>();
                proportionMap.put(loserPlayScore.toPlainString(), map);
            }

            BigDecimal proportion;
            if (player.getCalculateScore().compareTo(loserPlayScore) > 0) {
                proportion = loserPlayScore.divide(total, SCALE, ROUNDING_MODE);
            } else {
                proportion = player.getCalculateScore().divide(total, SCALE, ROUNDING_MODE);
            }

            map.put(player, proportion);
        }

    }

    public void refreshPlayer() {
        informPlayer((p) -> {
            return playerMap.get(p.getUser());
        }, (p) -> {
            if (p.getStatus() == PokerChannelStatusEnum.DISCONNECT.getStatus()) {
                return false;
            }
            return true;
        });
    }

    public void informPlayer(Function<PokerChannel, BaseBo> getMesssageFun, Function<PokerChannel, Boolean> filter) {
        eventLoop.execute(() -> {
            pokerChannelList.forEach(pokerChannel -> {
                if (filter == null || filter.apply(pokerChannel)) {
                    BaseBo bo = getMesssageFun.apply(pokerChannel);
                    pokerChannel.getChannel().writeAndFlush(bo);
                }
            });
        });

    }

    public GameRound getGameRound() {
        return gameRound;
    }

    public int getRoomId() {
        return roomId;
    }

    public void disconnect(PokerChannel pokerChannel) {
        pokerChannel.setStatus(PokerChannelStatusEnum.DISCONNECT.getStatus());
        Player player = playerMap.get(pokerChannel.getUser());

        if (nowOperate == pokerChannel && gameRound.getStatus() == GameRoundStatusEnum.ACTIVITY.getStatus()) {
            nowOperate = null;
            Operate operate = new Operate();
            operate.setOperate(OperateEnum.FOLD.getOperate());
            operateHandle(operate, pokerChannel);
        } else {
            player.setStatus(PlayerStatusEnum.FOLD.getStatus());
        }
    }

}
