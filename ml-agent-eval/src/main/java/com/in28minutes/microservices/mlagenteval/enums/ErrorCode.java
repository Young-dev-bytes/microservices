package com.in28minutes.microservices.mlagenteval.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ErrorCode {


    INSURED_ID_TYPE_ERROR(18002, "200","您选择的被投保人证件类型与号码不匹配，请重新选择");


    private int httpStatus;
    private String code;

    private Object message;

    public int getHttpStatus() {
        return 0;
    }

    public String getCode() {
        return null;
    }

    public Object getMessage() {
        return message;
    }
}
