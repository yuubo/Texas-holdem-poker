package org.example.common.enume;

public enum PrintColorEnum {
    RED("\033[31m"), //红色
    GREEN("\033[32m"), //绿色
    YELLOW("\033[33m"), //黄色
    BLUE("\033[34m"), //蓝色
    PURPLE("\033[35m"), //紫色
    CYAN("\033[36m"), //青色
    WHITE("\033[37m"), //白色
    RESET("\033[0m");

    private String value;

    PrintColorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
