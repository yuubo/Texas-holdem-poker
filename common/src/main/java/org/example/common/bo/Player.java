package org.example.common.bo;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.math.BigDecimal;
import java.util.List;

@JsonIdentityInfo(scope=Player.class,generator = ObjectIdGenerators.PropertyGenerator.class, property="id")
public class Player extends BaseBo {

    private int id;

    private User user;

    private List<Poker> pokers;

    private GameRound gameRound;

    /**
     * 玩家总分数
     */
    private int scoreTotal;

    /**
     * 玩家下注
     */
    private int score;

    /**
     * 计算得分 每局清零
     */
    private transient BigDecimal calculateScore;

    /**
     * 计算得分 每局清零
     */
    private transient BigDecimal calculateTotalScore;

    /**
     * 牌等级分数 每局清零
     */
    private int grade;

    /**
     * 0:普通 1:大盲注 2:小盲注 3:加注 4:弃牌
     * {@link org.example.common.enume.PlayerStatusEnum}
     */
    private int status;

    /**
     * 0:普通，等待别人操作  1:活动，自己操作
     */
    private int activity;

    /**
     * 当前局玩家赢得积分 每局清零
     */
    private int partyWinScore;

    /**
     * 计算得分 每局清零
     */
    private transient BigDecimal calculatePartyWinScore;

    /**
     * 超出的下注，需要退回 每局清零
     */
    private transient BigDecimal excessiveScore;

    /**
     * 当前局玩家的牌型
     */
    private int pokerType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Poker> getPokers() {
        return pokers;
    }

    public void setPokers(List<Poker> pokers) {
        this.pokers = pokers;
    }

    public GameRound getGameRound() {
        return gameRound;
    }

    public void setGameRound(GameRound gameRound) {
        this.gameRound = gameRound;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getActivity() {
        return activity;
    }

    public void setActivity(int activity) {
        this.activity = activity;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getPartyWinScore() {
        return partyWinScore;
    }

    public void setPartyWinScore(int partyWinScore) {
        this.partyWinScore = partyWinScore;
    }

    public int getPokerType() {
        return pokerType;
    }

    public void setPokerType(int pokerType) {
        this.pokerType = pokerType;
    }

    public BigDecimal getCalculateScore() {
        return calculateScore;
    }

    public void setCalculateScore(BigDecimal calculateScore) {
        this.calculateScore = calculateScore;
    }

    public BigDecimal getCalculateTotalScore() {
        return calculateTotalScore;
    }

    public void setCalculateTotalScore(BigDecimal calculateTotalScore) {
        this.calculateTotalScore = calculateTotalScore;
    }

    public BigDecimal getCalculatePartyWinScore() {
        return calculatePartyWinScore;
    }

    public void setCalculatePartyWinScore(BigDecimal calculatePartyWinScore) {
        this.calculatePartyWinScore = calculatePartyWinScore;
    }

    public BigDecimal getExcessiveScore() {
        return excessiveScore;
    }

    public void setExcessiveScore(BigDecimal excessiveScore) {
        this.excessiveScore = excessiveScore;
    }
}
