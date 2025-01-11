package com.young.microservices.mlagenteval.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AgentEvalJobExecReq {
    @NotBlank
    private String jobId;
}
