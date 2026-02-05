package org.example.common.enume;

public enum PokerTypeEnum {
     // 高牌 对子 两对 三张 顺子 同花 葫芦 四条 同花顺
    A(1, "高牌"),
    B(2, "对子"),
    C(3, "双对"),
    D(4, "三张"),
    E(5, "顺子"),
    F(6, "同花"),
    G(7, "葫芦"),
    H(8, "四条"),
    I(9, "同花顺")
    ;

    private int type;

    private String value;

    PokerTypeEnum(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public static String getPokerTypeEnum(int type) {
        for (PokerTypeEnum pokerTypeEnum : values()) {
            if (pokerTypeEnum.type == type) {
                return pokerTypeEnum.value;
            }
        }
        return null;
    }
}
