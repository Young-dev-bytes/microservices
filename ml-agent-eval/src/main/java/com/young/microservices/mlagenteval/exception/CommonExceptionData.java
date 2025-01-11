package com.young.microservices.mlagenteval.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommonExceptionData {

    private String code;

    private String msg;

    public CommonExceptionData(String msg) {
        this.msg = msg;
    }
}
