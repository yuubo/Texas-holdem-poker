package org.example.service.game;

import org.example.common.message.Player;

import java.util.concurrent.atomic.AtomicInteger;

public class GameIndex {
    //牌局,每局结束时+1 不清零
    private int partyIndex = 0;

    //每轮最后一个加注玩家 每局清零
    private volatile LastPlayer lastPlayer = new LastPlayer();

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

    public int getPartyPlayerCount() {
        return partyPlayerCount;
    }

    public void setPartyPlayerCount(int i) {
        this.partyPlayerCount = i;
    }

    public LastPlayer getLastPlayer() {
        return lastPlayer;
    }

    public void setLastPlayer(LastPlayer lastPlayer) {
        this.lastPlayer = lastPlayer;
    }

    @Override
    public String toString() {
        return "GameIndex{" +
                "partyIndex=" + partyIndex +
                ", partyPlayerCount=" + partyPlayerCount +
                ", playIndex=" + playIndex +
                ", lastPlayer=" + lastPlayer.index() +
                ", player=" + lastPlayer.player().getUser().getName() +
                '}';
    }

    public class LastPlayer {
        private Player player;
        private int index;
        public LastPlayer() {
            this.player = player;
            this.index = index;
        }
        public Player player() {
            return player;
        }
        public int index() {
            return index;
        }

        public LastPlayer player(Player player) {
            this.player = player;
            return this;
        }
        public LastPlayer index(int index) {
            this.index = index;
            return this;
        }
    }
}
