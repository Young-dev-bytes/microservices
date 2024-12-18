package com.in28minutes.microservices.mlagenteval.dto;

import lombok.Data;

import java.util.List;

@Data
public class AgentEvalJobTrackDownReq {
    private List<String> trackIdList;

    private String instanceId;
}
