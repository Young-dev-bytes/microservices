package com.in28minutes.microservices.mlagenteval.exception;

import com.in28minutes.microservices.mlagenteval.enums.ErrorCode;

public class ServerInnerException extends InvocatException {
    private static final long serialVersionUID = -8546011117379992834L;
    private final ErrorCode errorCode;

    public ServerInnerException(ErrorCode errorCode) {
        super(errorCode.getHttpStatus(), errorCode.getCode(), errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ServerInnerException(ErrorCode errorCode, String message) {
        super(errorCode.getHttpStatus(), errorCode.getCode(), message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return this.errorCode;
    }
}