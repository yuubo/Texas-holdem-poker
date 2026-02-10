package org.example.common.enume;

public enum GameRoundStatusEnum {

    /**
     * 游戏进行中
     */
    ACTIVITY(1), //进行中

    /**
     * 游戏未开始或本轮结束
     */
    FINISH(0); //结束

    private int status;

    GameRoundStatusEnum(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

}
