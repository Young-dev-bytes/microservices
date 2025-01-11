package com.young.microservices.mlagenteval.dto;

import com.young.microservices.mlagenteval.common.PageBaseReq;
import lombok.Data;

@Data
public class AgentEvalTaskPageReq extends PageBaseReq {
    private String createUser;

    private String evalTaskName;

    private String tenantId;

    private String projectId;
}
