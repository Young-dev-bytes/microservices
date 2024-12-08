package com.in28minutes.microservices.mlagenteval.dto;

import lombok.Data;

@Data
public class AgentEvalJobPageReq  {
    private String createUser;

    private String taskId;

    private String tenantId;

    private String projectId;
}
