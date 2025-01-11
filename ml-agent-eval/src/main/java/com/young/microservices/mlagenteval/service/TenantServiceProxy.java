package com.young.microservices.mlagenteval.service;

import com.young.microservices.mlagenteval.common.CommonResponse;
import org.springframework.stereotype.Service;

@Service
public class TenantServiceProxy {
    public CommonResponse occupyDevice(String deviceUdid, String operator, String id) {
        return null;
    }

    public void freeDevice(String deviceUdid) {

    }
}
