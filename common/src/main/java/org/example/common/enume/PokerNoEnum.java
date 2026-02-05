package org.example.common.enume;

public enum PokerNoEnum {
    J("J", 11), Q("Q", 12), K("K", 13), A("A", 14);

    private String value;

    private int number;

    PokerNoEnum(String value, int number) {
        this.value = value;
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public String getValue() {
        return value;
    }

    public static String getValue(int type) {
        if (type < 11) {
            return String.valueOf(type);
        }
        PokerNoEnum pokerSuitsEnum = values()[type - 11];
        if (pokerSuitsEnum != null) {
            return pokerSuitsEnum.value;
        }
        return String.valueOf(type);
    }

}
