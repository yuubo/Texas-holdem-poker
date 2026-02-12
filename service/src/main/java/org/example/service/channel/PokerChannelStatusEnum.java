package org.example.service.channel;

public enum PokerChannelStatusEnum {

    /**
     * 正常
     */
    NORMAL(0), //正常

    /**
     * 断开
     */
    DISCONNECT(1); //断开

    private int status;

    PokerChannelStatusEnum(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

}
