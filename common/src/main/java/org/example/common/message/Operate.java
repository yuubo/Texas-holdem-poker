package org.example.common.message;

import java.util.List;

public class Operate extends BaseBo {

    private int operate;

    /**
     * 加注的筹码
     */
    private int score;

    /**
     * 允许玩家操作的指令
     */
    private List<Integer> allowOperates;

    public int getOperate() {
        return operate;
    }

    public void setOperate(int operate) {
        this.operate = operate;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<Integer> getAllowOperates() {
        return allowOperates;
    }

    public void setAllowOperates(List<Integer> allowOperates) {
        this.allowOperates = allowOperates;
    }
}
