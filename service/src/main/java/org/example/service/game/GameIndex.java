package org.example.service.game;

import java.util.concurrent.atomic.AtomicInteger;

public class GameIndex {
    //牌局,每局结束时+1 不清零
    private int partyIndex = 0;

    //每轮最后一个加注玩家 每局清零
    private int fillLastPlayIndex = 0;

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
}
