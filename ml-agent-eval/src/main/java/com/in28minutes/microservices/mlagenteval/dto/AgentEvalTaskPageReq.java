package com.in28minutes.microservices.mlagenteval.dto;

import lombok.Data;

@Data
public class AgentEvalTaskPageReq {

    private String createUser;

    private String evalTaskName;

    private String tenantId;

    private String projectId;
}
