package org.example.service.context;

import org.example.common.bo.Player;
import org.example.common.bo.Poker;
import org.example.common.enume.PokerTypeEnum;

import java.util.*;

public class PokerUtils {

    public static void createPoker(List<Poker> pokerList) {
        pokerList.clear();
        for (int i = 2; i <= 14; i++) {
            for (int j = 0; j < 4; j++) {
                Poker poker = new Poker();
                poker.setNo(i);
                poker.setSuits(j);
                pokerList.add(poker);
            }
        }
    }

    public static List<Poker> getPoker(int limit, List<Poker> pokerList) {
        List<Poker> list = new ArrayList<>(limit);
        Random random = new Random();
        for (int i = 0; i < limit; i++) {
            int index = random.nextInt(pokerList.size());
            list.add(pokerList.remove(index));
        }
        return list;
    }

    public static void grade(List<Poker> pkList, Player player) {
        //从大到小排序
        Collections.sort(pkList, (o1, o2) -> o2.getNo() - o1.getNo());

        List<List<Poker>> pl = new ArrayList<>();
        Map<Integer, Integer> suitsMap = new HashMap<>();
        Boolean isCheckStraightFlush = null; //是否检查同花顺
        int checkStraightFlushIndex = 0; //同花顺检查索引
        Poker straightMax = null;
        for (int i = 0; i < pkList.size(); i++) {
            Poker poker = pkList.get(i);

            /*顺子检查*/
            if (i < 3 && ((poker.getNo() - 4) == pkList.get(i + 4).getNo())) {
                straightMax = poker;
                isCheckStraightFlush = true;
                checkStraightFlushIndex = i;
            }

            /*如果是顺子则检查后面5张牌花色是否相同*/
            if (straightMax != null && i - checkStraightFlushIndex < 5) {
                if (straightMax.getSuits() == poker.getSuits()
                        && isCheckStraightFlush) {
                    isCheckStraightFlush = true;
                } else {
                    isCheckStraightFlush = false;
                }
            }

            /*将数字相同的牌放在一起*/
            List<Poker> list = null;
            if (pl.isEmpty()) {
                list = new ArrayList<>();
                pl.add(list);
            } else {
                list = pl.getLast();
                if (list.getFirst().getNo() != poker.getNo()) {
                    list = new ArrayList<>();
                    pl.add(list);
                }
            }
            list.add(poker);

            /*统计每个花色出现次数*/
            if (suitsMap.containsKey(poker.getSuits())) {
                suitsMap.put(poker.getSuits(), suitsMap.get(poker.getSuits()) + 1);
            } else {
                suitsMap.put(poker.getSuits(), 1);
            }
        }

        int score = 0;
        Collections.sort(pl, (o1, o2) -> o2.size() - o1.size());
        /**
         *
         * 单牌+0 对子+100 两对+200 三张+300 顺子+400 同花+500 葫芦+1000 四条+2000 同花顺+3000
         */
        if (pl.size() == 7) {
            //单牌 顺子 同花 同花顺
            if (straightMax != null) {
                //顺子
                if (isCheckStraightFlush) {
                    //同花顺
                    player.setPokerType(PokerTypeEnum.I.getType());
                    score = pl.getFirst().getFirst().getNo() + 3000;
                } else {
                    //顺子
                    player.setPokerType(PokerTypeEnum.E.getType());
                    score = pl.getFirst().getFirst().getNo() + 400;
                }
            } else if (suitsMap.values().stream().filter(i -> i == 5).count() == 1) {
                //同花
                player.setPokerType(PokerTypeEnum.F.getType());
                score = 500;
            } else {
                //单牌
                player.setPokerType(PokerTypeEnum.A.getType());
                score = pkList.getFirst().getNo();
            }
        } else if (pl.size() == 6) {
            //对子
            player.setPokerType(PokerTypeEnum.B.getType());
            score = pl.getFirst().getFirst().getNo() + 100;
        } else if (pl.size() == 5) {
            //两对 三张
            if (pl.getFirst().size() == 2) {
                //两对
                player.setPokerType(PokerTypeEnum.C.getType());
                score = pl.getFirst().getFirst().getNo() + 200;
            } else {
                //三张
                player.setPokerType(PokerTypeEnum.D.getType());
                score = pl.getFirst().getFirst().getNo() + 300;
            }
        } else if (pl.size() == 4) {
            //葫芦 四条

            if (pl.getFirst().size() == 3) {
                //葫芦
                player.setPokerType(PokerTypeEnum.G.getType());
                score = pl.getFirst().getFirst().getNo() * 20 + 1000;
                score += pl.getLast().getFirst().getNo();
            } else {
                //四条
                player.setPokerType(PokerTypeEnum.H.getType());
                score = pl.getFirst().getFirst().getNo() + 2000;
            }
        }
        player.setGrade(score);
        //return score;
    }
}
