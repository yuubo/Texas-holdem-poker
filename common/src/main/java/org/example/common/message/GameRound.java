package org.example.common.message;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.math.BigDecimal;
import java.util.List;

@JsonIdentityInfo(scope=GameRound.class,generator = ObjectIdGenerators.PropertyGenerator.class, property="id")
public class GameRound extends BaseBo {

    private int id;

    private List<Poker> commonPokerList;

    private List<Player> playerList;

    private int index;

    /**
     * 状态 0:牌局进行中 1:牌局结束
     */
    private int status;

    /**
     * 所有玩家总下注 每局清零
     */
    private int scoreTotal;

    /**
     * 所有玩家总得分 每局清零
     */
    private transient BigDecimal calculateScoreTotal;

    /**
     * 当前轮最高注 每局清零
     */
    private int score;

    /**
     * 所有玩家得分 每局清零
     */
    private transient BigDecimal calculateScore;

    /**
     * 当前局弃牌玩家数 每局清零
     */
    private int foldPlayerTCount;

    /**
     * 当前局是否只有一人未弃牌
     */
    private transient boolean isOnlyOne = false;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<Player> playerList) {
        this.playerList = playerList;
    }

    public List<Poker> getCommonPokerList() {
        return commonPokerList;
    }

    public void setCommonPokerList(List<Poker> commonPokerList) {
        this.commonPokerList = commonPokerList;
    }

    public int getScoreTotal() {
        return scoreTotal;
    }

    public void setScoreTotal(int scoreTotal) {
        this.scoreTotal = scoreTotal;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getFoldPlayerTCount() {
        return foldPlayerTCount;
    }

    public void setFoldPlayerTCount(int foldPlayerTCount) {
        this.foldPlayerTCount = foldPlayerTCount;
    }

    public BigDecimal getCalculateScoreTotal() {
        return calculateScoreTotal;
    }

    public void setCalculateScoreTotal(BigDecimal calculateScoreTotal) {
        this.calculateScoreTotal = calculateScoreTotal;
    }

    public BigDecimal getCalculateScore() {
        return calculateScore;
    }

    public void setCalculateScore(BigDecimal calculateScore) {
        this.calculateScore = calculateScore;
    }

    public boolean isOnlyOne() {
        return isOnlyOne;
    }

    public void setOnlyOne(boolean isOnlyOne) {
        this.isOnlyOne = isOnlyOne;
    }
}
