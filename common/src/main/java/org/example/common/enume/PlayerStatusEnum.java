package org.example.common.enume;

public enum PlayerStatusEnum {
    NORMAL(0), //普通
    BIG_BLIND(1), //大盲注
    SMALL_BLIND(2), //小盲注
    FILL(3), //加注
    FOLD(4), //弃牌
    ALL_IN(5), //全压
    PASS(6), //过牌
    CALL(7), //跟注
    ;
    private int status;

    PlayerStatusEnum(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

}
