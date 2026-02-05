package org.example.common.enume;

public enum OperateEnum {
    START(1, "开始"), //开始
    PASS(2, "过牌"), //过
    CALL(3, "跟注"), //跟注
    FILL(4, "加注"), //加注
    ALLIN(5, "all-in"), //全注
    FOLD(6, "弃牌"), //弃牌
    ;

    private int operate;

    private String explain;

    OperateEnum(int operate, String explain) {
        this.operate = operate;
        this.explain = explain;
    }

    public int getOperate() {
        return operate;
    }

    public String getExplain() {
        return explain;
    }

    public static OperateEnum getOperate(int operate) {
        for (OperateEnum value : values()) {
            if (value.operate == operate) {
                return value;
            }
        }
        return null;
    }
}
