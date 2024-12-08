package com.in28minutes.microservices.currencyconversionservice.controller;

import com.in28minutes.microservices.currencyconversionservice.common.BaseResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
public class HelloController {


    @GetMapping("/hello")
    public BaseResponse hello() throws ExecutionException {
        return BaseResponse.success("Hello, Young");
    }
}