package com.in28minutes.microservices.mlagenteval.dto;

import com.in28minutes.microservices.mlagenteval.common.PageBaseReq;
import lombok.Data;

@Data
public class AgentEvalJobInstancePageReq extends PageBaseReq {
    private String jobId;
}
