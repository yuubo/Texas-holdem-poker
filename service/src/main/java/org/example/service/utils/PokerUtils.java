package org.example.service.utils;

import org.example.common.message.Player;
import org.example.common.message.Poker;
import org.example.common.enume.PokerTypeEnum;

import java.util.*;

/**
 * 扑克牌牌按次序生成，发牌时用随机数抽取
 */
public class PokerUtils {

    public static void createPoker(List<Poker> pokerList) {
        for (int i = 2; i <= 14; i++) {
            for (int j = 0; j < 4; j++) {
                int finalJ = j;
                int finalI = i;
                pokerList.add(new Poker() {
                    {
                        this.setNo(finalI);
                        this.setSuits(finalJ);
                    }
                });
            }
        }
    }

    /**
     * 随机抽取牌，抽取的牌会从pokerList中移除
     * @param limit
     * @param pokerList
     * @return
     */
    public static List<Poker> getPoker(int limit, List<Poker> pokerList) {
        List<Poker> list = new ArrayList<>(limit);
        Random random = new Random();
        for (int i = 0; i < limit; i++) {
            int index = random.nextInt(pokerList.size());
            list.add(pokerList.remove(index));
        }
        return list;
    }

    /**
     * 计算玩家得分
     * @param pkList
     * @param player
     */
    public static void grade(List<Poker> pkList, Player player) {
        //从大到小排序
        Collections.sort(pkList, (o1, o2) -> o2.getNo() - o1.getNo());

        ScoreData sd = new ScoreData();
        //整理牌型数据
        disposal(pkList, sd);

        if (Boolean.TRUE != sd.isStraight) {
            Collections.sort(sd.pl, (o1, o2) -> o2.size() - o1.size());
        }

        //根据牌型计算得分
        calculateScore(sd, player);

    }

    /**
     * 整理牌型数据
     */
    private static void disposal(List<Poker> pkList, ScoreData sd) {
        for (int i = 0; i < pkList.size(); i++) {
            Poker poker = pkList.get(i);

            /*顺子、同花顺检查*/
            checkStraight(pkList, sd, poker, i);

            /*将数字相同的牌放在一起*/
            List<Poker> list = null;
            if (sd.pl.isEmpty()) {
                list = new ArrayList<>();
                sd.pl.add(list);
            } else {
                list = sd.pl.getLast();
                if (list.getFirst().getNo() != poker.getNo()) {
                    list = new ArrayList<>();
                    sd.pl.add(list);
                }
            }
            list.add(poker);

            /*统计每个花色出现次数*/
            if (sd.suitsMap.containsKey(poker.getSuits())) {
                sd.suitsMap.put(poker.getSuits(), sd.suitsMap.get(poker.getSuits()) + 1);
            } else {
                sd.suitsMap.put(poker.getSuits(), 1);
            }
        }
    }

    /**
     * 顺子检查
     */
    private static void checkStraight(List<Poker> pkList, ScoreData sd, Poker poker, int i) {
        if (i < 3 && sd.isStraight == null) {
            sd.straightSuitsMap.put(poker.getSuits(), 1);
            sd.straightList.add(poker);
            Poker pPre = null;
            int checkCount = 1;
            for (int j = 1;;j++) {
                int iNext = i + j;
                if (i + j >= pkList.size()) {
                    sd.isStraight = false;
                    return;
                }

                Poker pNext = pkList.get(iNext);
                if (pPre != null && pPre.getNo() == pNext.getNo()) {
                    continue;
                }

                if (poker.getNo() != (pNext.getNo() + checkCount)) {
                    if (i == 2) {
                        sd.isStraight = false;
                    }
                    sd.straightSuitsMap.clear();
                    sd.straightList.clear();
                    return;
                }

                if (sd.straightSuitsMap.containsKey(pNext.getSuits())) {
                    sd.straightSuitsMap.put(pNext.getSuits(), sd.straightSuitsMap.get(pNext.getSuits()) + 1);
                } else {
                    sd.straightSuitsMap.put(pNext.getSuits(), 1);
                }

                checkCount ++;
                sd.straightList.add(poker);
                if (checkCount == 5) {
                    sd.straightMax = poker;
                    sd.isStraight = true;
                    Integer max = sd.straightSuitsMap.values().stream().max(Integer::compareTo).get();
                    if (max == 5) {
                        sd.isStraightSuits = true;
                    }
                    return;
                }
                pPre = pNext;
            }
        }
    }

    /**
     * 计算得分
     * 单牌+0 对子+100 两对+200 三张+300 顺子+400 同花+500 葫芦+1000 四条+2000 同花顺+3000
     */
    public static void calculateScore(ScoreData sd, Player player) {
        if (straightFlush(sd, player)) {
            //同花顺
            return;
        }
        if (sd.pl.getFirst().size() == 4) {
            //四条
            player.setPokerType(PokerTypeEnum.H.getType());
            player.setGrade(sd.pl.getFirst().getFirst().getNo() + 2000);
            player.setWinPokers(sd.pl.getFirst());
            return;
        }
        if (sd.pl.getFirst().size() == 3) {
            if (sd.pl.get(1).size() > 1) {
                //葫芦
                player.setPokerType(PokerTypeEnum.G.getType());
                int score = sd.pl.getFirst().getFirst().getNo() * 20 + 1000;
                score += sd.pl.getLast().getFirst().getNo();
                player.setGrade(score);
                player.setWinPokers(sd.pl.getFirst());
                player.getWinPokers().addAll(sd.pl.get(1).subList(0, 2));
                return;
            } else {
                //同花或顺子
                if (sameSuit(sd, player) || straight(sd, player)) {
                    return;
                }
                //三张
                player.setPokerType(PokerTypeEnum.D.getType());
                player.setGrade(sd.pl.getFirst().getFirst().getNo() + 300);
                player.setWinPokers(sd.pl.getFirst());
                return;
            }
        } else {
            //同花或顺子
            if (sameSuit(sd, player) || straight(sd, player)) {
                return;
            }

            if (sd.pl.getFirst().size() == 2) {
                if (sd.pl.get(1).size() == 2) {
                    //两对
                    player.setPokerType(PokerTypeEnum.C.getType());
                    player.setGrade(sd.pl.getFirst().getFirst().getNo() + 200);
                    player.setWinPokers(sd.pl.getFirst());
                    player.getWinPokers().addAll(sd.pl.get(1));
                    return;
                } else {
                    //对子
                    player.setPokerType(PokerTypeEnum.B.getType());
                    player.setGrade(sd.pl.getFirst().getFirst().getNo() + 100);
                    player.setWinPokers(sd.pl.getFirst());
                    return;
                }
            }

            if (sd.pl.getFirst().size() == 1) {
                //单牌
                player.setPokerType(PokerTypeEnum.A.getType());
                player.setGrade(sd.pl.getFirst().getFirst().getNo());
                player.setWinPokers(sd.pl.getFirst());
                return;
            }
        }

        throw new RuntimeException("未知牌型");
    }

    /**
     * 同花顺
     */
    public static boolean straightFlush(ScoreData sd, Player player) {
        if (sd.isStraightSuits) {
            //同花顺
            player.setPokerType(PokerTypeEnum.I.getType());
            player.setGrade(sd.pl.getFirst().getFirst().getNo() + 3000);
            return true;
        }
        return false;
    }

    /**
     * 同花
     */
    public static boolean sameSuit(ScoreData sd, Player player) {
        Integer max = sd.suitsMap.values().stream().max(Integer::compareTo).get();
        if (max > 5) {
            //同花
            player.setPokerType(PokerTypeEnum.F.getType());
            player.setGrade(sd.pl.getFirst().getFirst().getNo() + 500);
            return true;
        }
        return false;
    }

    /**
     * 顺子
     */
    public static boolean straight(ScoreData sd, Player player) {
        //顺子 同花 同花顺
        if (Boolean.TRUE == sd.isStraight) {
            //顺子
            player.setPokerType(PokerTypeEnum.E.getType());
            player.setGrade(sd.pl.getFirst().getFirst().getNo() + 400);
            return true;
        }

        return false;
    }

    private static class ScoreData {

        /**
         * 相同数字牌型分类
         */
        final List<List<Poker>> pl = new ArrayList<>();

        /**
         * 统计每个花色出现的次数
         */
        final Map<Integer, Integer> suitsMap = new HashMap<>();

        /**
         * 判断顺子是否花色相同
         */
        final Map<Integer, Integer> straightSuitsMap = new HashMap<>();

        /**
         * 顺子
         */
        final List<Poker> straightList = new ArrayList<>();

        /**
         * 顺子中最大的牌
         */
        Poker straightMax = null;

        /**
         * 是否是顺子
         */
        Boolean isStraight = null;

        /**
         * 是否同花顺
         */
        boolean isStraightSuits = false;
    }

}
