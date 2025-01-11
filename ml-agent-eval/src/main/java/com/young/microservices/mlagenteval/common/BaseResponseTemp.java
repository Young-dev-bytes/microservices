package com.young.microservices.mlagenteval.common;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.young.microservices.mlagenteval.enums.SystemErrorEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Slf4j
public class BaseResponseTemp<T> {

    private static final int CODE_SUCCESS = 0;
    private static final int CODE_FAIL = -1;

    private int code;
    private String errorCode;
    private String errorMsg;
    private T data;

    public static <T> BaseResponseTemp<T> success(T data) {
        BaseResponseTemp<T> baseResponse = new BaseResponseTemp<>();
        baseResponse.setCode(CODE_SUCCESS);
        baseResponse.setData(data);
        return baseResponse;
    }

    public static <T> BaseResponseTemp<T> success() {
        return success(null);
    }

    public static <T> BaseResponseTemp<T> fail(String errorCode, String errorMsg, T data) {
        BaseResponseTemp<T> baseResponse = new BaseResponseTemp<>();
        baseResponse.setCode(CODE_FAIL);
        baseResponse.setErrorCode(errorCode);
        baseResponse.setErrorMsg(errorMsg);
        baseResponse.setData(data);
        return baseResponse;
    }

    public static <T> BaseResponseTemp<T> fail(String errorCode, String errorMsg) {
        return fail(errorCode, errorMsg, null);
    }

    public static <T> BaseResponseTemp<T> fail(SystemErrorEnum insErrorEnum) {
        if (null == insErrorEnum.getErrorMsg()) {
            log.error("未知错误，需检查程序");
            insErrorEnum.setErrorMsg("未知错误");
        }
        return fail(insErrorEnum.getErrorCode(), insErrorEnum.getErrorMsg());
    }
}
