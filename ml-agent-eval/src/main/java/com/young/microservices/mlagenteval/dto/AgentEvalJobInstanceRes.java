package com.young.microservices.mlagenteval.dto;

import com.young.microservices.mlagenteval.dao.entity.AgentEvalJobInstanceDo;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AgentEvalJobInstanceRes extends AgentEvalJobInstanceDo {

    private String jobStatusDesc;

    private String executeUser;

    private String jobName;

    private String deviceName;

    private LocalDateTime startTime;
}
