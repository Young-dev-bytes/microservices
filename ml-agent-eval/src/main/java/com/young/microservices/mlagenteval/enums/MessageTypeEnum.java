package com.young.microservices.mlagenteval.enums;

import lombok.Getter;

@Getter
public enum MessageTypeEnum {

    MESSAGE("message", "正常信息"),

    SERVER_ERROR("server_error", "错误信息");

    private String value;

    private String desc;

    MessageTypeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
