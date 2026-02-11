package org.example.service.game;

import java.util.concurrent.atomic.AtomicInteger;

public class GameIndex {
    //牌局,每局结束时+1 不清零
    private int partyIndex = 0;

    //每轮最后一个加注玩家 每局清零
    private volatile int fillLastPlayIndex = 0;

    /**
     * 参与当前牌局玩家的数量 每局清零
     */
    private int partyPlayerCount;

    //玩家操作索引，每局清零
    private AtomicInteger playIndex = new AtomicInteger();

    public int getPlayIndex() {
        return playIndex.get();
    }

    public void playIndexAdd() {
        playIndex.incrementAndGet();
    }

    public void setPlayIndex(int i) {
        playIndex.set(i);
    }

    public int getPartyIndex() {
        return partyIndex;
    }

    public void partyIndexAdd() {
        this.partyIndex ++;
    }

    public int getFillLastPlayIndex() {
        return fillLastPlayIndex;
    }

    public void setFillLastPlayIndex(int i) {
        this.fillLastPlayIndex = i;
    }

    public int getPartyPlayerCount() {
        return partyPlayerCount;
    }

    public void setPartyPlayerCount(int i) {
        this.partyPlayerCount = i;
    }

    @Override
    public String toString() {
        return "GameIndex{" +
                "partyIndex=" + partyIndex +
                ", fillLastPlayIndex=" + fillLastPlayIndex +
                ", partyPlayerCount=" + partyPlayerCount +
                ", playIndex=" + playIndex +
                '}';
    }
}
