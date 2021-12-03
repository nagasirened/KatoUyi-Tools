package com.katouyi.tools.mate.common.method;

import lombok.Getter;

/**
 * 公共状态，一般用来表示开启和关闭
 */
public enum StatusEnum {

    /**
     * 启用
     */
    ENABLE("enable", 1),

    /**
     * 禁用
     */
    DISABLE("disable", 0);

    private final String type;
    private final Integer code;

    StatusEnum(String type, Integer code) {
        this.type = type;
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public Integer getCode() {
        return code;
    }
}
