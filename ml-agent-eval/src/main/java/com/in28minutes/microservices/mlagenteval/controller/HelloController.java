package com.in28minutes.microservices.mlagenteval.controller;

import com.in28minutes.microservices.mlagenteval.common.BaseResponseTemp;
import com.in28minutes.microservices.mlagenteval.common.CommonResponse;
import com.in28minutes.microservices.mlagenteval.enums.ErrorCode;
import com.in28minutes.microservices.mlagenteval.enums.SystemErrorEnum;
import com.in28minutes.microservices.mlagenteval.exception.BusinessException;
import com.in28minutes.microservices.mlagenteval.exception.MlAgentEvalError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public CommonResponse hello() throws ExecutionException {
        return CommonResponse.successResponse("Hello, Young");
    }
}