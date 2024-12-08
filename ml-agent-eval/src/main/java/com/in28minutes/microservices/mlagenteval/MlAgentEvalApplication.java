package com.in28minutes.microservices.mlagenteval;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients("com.in28minutes.microservices.mlagenteval")
@EnableDiscoveryClient
public class MlAgentEvalApplication {

    public static void main(String[] args) {
        SpringApplication.run(MlAgentEvalApplication.class, args);
    }

}
