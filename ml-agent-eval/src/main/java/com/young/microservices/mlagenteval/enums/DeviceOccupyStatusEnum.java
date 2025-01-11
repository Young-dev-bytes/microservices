package com.young.microservices.mlagenteval.enums;

import lombok.Getter;

@Getter
public enum DeviceOccupyStatusEnum {
    /**
     * 未占用
     */
    NOOCCUPY("noOccupy"),
    /**
     * 已占用
     */
    OCCUPIED("occupied");

    private String value;

    DeviceOccupyStatusEnum(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
