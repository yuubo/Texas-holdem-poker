package org.example.common.enume;

public enum GameRoundStatusEnum {

    ACTIVITY(0), //进行中

    FINISH(1); //结束

    private int status;

    GameRoundStatusEnum(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

}
