package org.example.common.enume;

public enum PlayerActivityEnum {
    NORMAL(0), //普通
    ACTIVITY(1),
    ;
    private int number;

    PlayerActivityEnum(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
