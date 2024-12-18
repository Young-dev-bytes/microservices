package com.in28minutes.microservices.mlagenteval.common;


//import com.hihonor.zeus.common.log.annotation.SecurityLogPolicy;
//import com.hihonor.zeus.common.log.enums.LogPrintPolicy;

import lombok.Data;
import org.slf4j.MDC;

@Data
public class BaseResponse {
    public static final Integer SUCCESS_STATUS = 200;
    public static final Integer FAILED_STATUS = 500;

    // @SecurityLogPolicy(policy = LogPrintPolicy.SHOW_ALL)
    private Integer code;

    // @SecurityLogPolicy(policy = LogPrintPolicy.SHOW_ALL)
    private String message;

    private String requestId = MDC.get("traceId");

    /**
     * BaseResponse
     */
    public BaseResponse() {
        setCode(SUCCESS_STATUS);
    }
}
