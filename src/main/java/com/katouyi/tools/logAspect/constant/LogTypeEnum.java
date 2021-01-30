package com.katouyi.tools.logAspect.constant;

/**
 * 日志类型的枚举
 */
public enum LogTypeEnum {

    OPERATION(1, "操作"),
    EXCEPTION(2, "异常"),
    LOGIN(3, "登录"),;

    private Integer type;

    private String typeName;

    LogTypeEnum(Integer type, String typeName) {
        this.type = type;
        this.typeName = typeName;
    }
}
