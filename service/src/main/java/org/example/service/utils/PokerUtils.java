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
        calculateScore(sd, player, pkList);

    }

    /**
     * 整理牌型数据
     */
    private static void disposal(List<Poker> pkList, ScoreData sd) {
        for (int i = 0; i < pkList.size(); i++) {
            Poker poker = pkList.get(i);

            /*顺子检查*/
            checkStraight(pkList, sd, poker, i);

            if (Boolean.TRUE == sd.isStraight) {
                /*如果满足顺子条件，则没必要再进行其他牌型处理*/
                break;
            }

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
            sd.straightSuitsSet.add(poker.getSuits());
            for (int j = 1; j < 5; j++) {
                Poker pNext = pkList.get(j + i);
                if (poker.getNo() != pNext.getNo() + j) {
                    if (i == 2) {
                        sd.straightSuitsSet.clear();
                        sd.isStraight = false;
                        break;
                    }
                    return;
                }
                sd.straightSuitsSet.add(pNext.getSuits());
                if (j == 4) {
                    sd.straightMax = poker;
                    sd.isStraight = true;
                }
            }
        }
    }

    /**
     * 计算得分
     * 单牌+0 对子+100 两对+200 三张+300 顺子+400 同花+500 葫芦+1000 四条+2000 同花顺+3000
     */
    private static void calculateScore(ScoreData sd, Player player, List<Poker> pkList) {
        if (!straight(sd, player))
            if (!highCard(sd, player, pkList))
                if (!pair(sd, player, pkList))
                    if (!doublePairAndThree(sd, player))
                        if (!gourdAndFour(sd, player))
                            throw new RuntimeException("牌型错误");
    }

    /**
     * 顺子
     */
    public static boolean straight(ScoreData sd, Player player) {
        if (Boolean.TRUE == sd.isStraight) {
            //顺子 同花顺
            if (sd.straightSuitsSet.size() == 1) {
                //同花顺
                player.setPokerType(PokerTypeEnum.I.getType());
                player.setGrade(sd.pl.getFirst().getFirst().getNo() + 3000);
            } else {
                //顺子
                player.setPokerType(PokerTypeEnum.E.getType());
                player.setGrade(sd.pl.getFirst().getFirst().getNo() + 400);
            }
            return true;
        }
        return false;
    }

    /**
     * 高牌
     */
    public static boolean highCard(ScoreData sd, Player player, List<Poker> pkList) {
        if (sd.pl.size() == 7) {
            //单牌
            player.setPokerType(PokerTypeEnum.A.getType());
            player.setGrade(pkList.getFirst().getNo());
            return true;
        }
        return false;
    }

    /**
     * 对子
     */
    public static boolean pair(ScoreData sd, Player player, List<Poker> pkList) {
        if (sd.pl.size() == 7) {
            //单牌
            player.setPokerType(PokerTypeEnum.A.getType());
            player.setGrade(pkList.getFirst().getNo());
            return true;
        }
        return false;
    }

    /**
     * 对子或三张
     */
    public static boolean doublePairAndThree(ScoreData sd, Player player) {
        if (sd.pl.size() == 5) {
            //两对 三张
            if (sd.pl.getFirst().size() == 2) {
                //两对
                player.setPokerType(PokerTypeEnum.C.getType());
                player.setGrade(sd.pl.getFirst().getFirst().getNo() + 200);
            } else {
                //三张
                player.setPokerType(PokerTypeEnum.D.getType());
                player.setGrade(sd.pl.getFirst().getFirst().getNo() + 300);
            }
            return true;
        }
        return false;
    }

    /**
     * 葫芦或四条
     */
    public static boolean gourdAndFour(ScoreData sd, Player player) {
        if (sd.pl.size() == 4) {
            //葫芦 四条
            if (sd.pl.getFirst().size() == 3) {
                //葫芦 三张牌面分乘20加基数再加两对牌面分 确保44433比333AA分数大
                player.setPokerType(PokerTypeEnum.G.getType());
                int score = sd.pl.getFirst().getFirst().getNo() * 20 + 1000;
                score += sd.pl.getLast().getFirst().getNo();
                player.setGrade(score);
            } else {
                //四条
                player.setPokerType(PokerTypeEnum.H.getType());
                player.setGrade(sd.pl.getFirst().getFirst().getNo() + 2000);
            }
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
        final Set<Integer> straightSuitsSet = new HashSet<>();

        /**
         * 顺子中最大的牌
         */
        Poker straightMax = null;

        /**
         * 是否是顺子
         */
        Boolean isStraight = null;
    }

}
