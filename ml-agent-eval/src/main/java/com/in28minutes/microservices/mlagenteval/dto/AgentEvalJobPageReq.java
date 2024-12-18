package com.in28minutes.microservices.mlagenteval.dto;

import com.in28minutes.microservices.mlagenteval.common.PageBaseReq;
import lombok.Data;

@Data
public class AgentEvalJobPageReq extends PageBaseReq {
    private String taskId;

    private String createUser;

    private String tenantId;

    private String projectId;
}
