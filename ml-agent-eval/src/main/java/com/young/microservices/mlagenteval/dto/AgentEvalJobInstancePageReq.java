package com.young.microservices.mlagenteval.dto;

import com.young.microservices.mlagenteval.common.PageBaseReq;
import lombok.Data;

@Data
public class AgentEvalJobInstancePageReq extends PageBaseReq {
    private String jobId;
}
