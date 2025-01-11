package com.young.microservices.mlagenteval.enums;

public enum OccupyTimeOutEnum {
    ONE_HOUR("1H", "1 hour");

    private String value;
    private String desc;

    OccupyTimeOutEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}
