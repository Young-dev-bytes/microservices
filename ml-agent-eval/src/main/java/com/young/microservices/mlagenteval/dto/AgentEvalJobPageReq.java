package com.young.microservices.mlagenteval.dto;

import com.young.microservices.mlagenteval.common.PageBaseReq;
import lombok.Data;

@Data
public class AgentEvalJobPageReq extends PageBaseReq {
    private String taskId;

    private String createUser;

    private String tenantId;

    private String projectId;
}
