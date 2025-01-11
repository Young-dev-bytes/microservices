package com.young.microservices.mlagenteval.enums;

import lombok.Getter;

@Getter
public enum EvalTaskTypeEnum {
    UIAGENT("UIAGENT", "ui agent eval type.");

    private String value;

    private String desc;

    EvalTaskTypeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
