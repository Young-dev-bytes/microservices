package com.young.microservices.mlagenteval.controller;

import com.young.microservices.mlagenteval.common.CommonResponse;
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