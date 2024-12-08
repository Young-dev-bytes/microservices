package com.in28minutes.microservices.mlagenteval.exception;

import com.in28minutes.microservices.mlagenteval.enums.ErrorCode;
import com.sun.jdi.InvocationException;

public class BusinessException extends InvocatException {
    private static final long serialVersionUID = -8546011117379992834L;
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getHttpStatus(), errorCode.getCode(), errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(errorCode.getHttpStatus(), errorCode.getCode(), message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return this.errorCode;
    }
}
