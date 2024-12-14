package com.in28minutes.microservices.mlagenteval.dto;

import lombok.Data;

import java.util.List;

@Data
public class AgentEvalInstTrackReq {
    private String instanceId;
    private String trackId;
    private List<InstanceTrackDetailInfo> trackDetailInfoList;
}
