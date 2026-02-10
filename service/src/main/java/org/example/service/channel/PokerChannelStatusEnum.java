package org.example.service.channel;

public enum PokerChannelStatusEnum {

    NORMAL(0), //正常

    DISCONNECT(1); //断开

    private int status;

    PokerChannelStatusEnum(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

}
