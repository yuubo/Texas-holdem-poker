package org.example.common.enume;

public enum PokerSuitsEnum {
    DIAMOND("♦", 0), HEART("♥", 1), SPADE("♠", 2), CLOVER("♣", 3);

    private String value;

    private int number;

    PokerSuitsEnum(String value, int number) {
        this.value = value;
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public String getValue() {
        return value;
    }

    public static String getValue(int suits) {
        PokerSuitsEnum pokerSuitsEnum = values()[suits];
        if (pokerSuitsEnum != null) {
            return pokerSuitsEnum.value;
        }
        return "";
    }

}
