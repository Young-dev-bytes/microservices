package com.young.microservices.mlagenteval.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ADBCMDReq {
    @NotBlank
    private String udId;
    @NotBlank
    private String cmd;

    private String screenshotPath;
}
