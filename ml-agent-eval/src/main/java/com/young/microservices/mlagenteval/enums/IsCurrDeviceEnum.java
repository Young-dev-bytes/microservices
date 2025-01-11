package com.young.microservices.mlagenteval.enums;

import lombok.Getter;

@Getter
public enum IsCurrDeviceEnum {

    ISCURRENT(1),

    NOCURRENT(0);

    private final Integer code;

    private String desc;

    IsCurrDeviceEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    IsCurrDeviceEnum(Integer code) {
        this.code = code;
    }
}
