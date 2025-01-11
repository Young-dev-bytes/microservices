package com.young.microservices.mlagenteval.enums;

import javax.ws.rs.core.Response;

public interface ErrorCode {
    String getCode();

    String getMessage();

    Response.Status getHttpStatus();
}
