package com.young.microservices.mlagenteval.dto;

import com.young.microservices.mlagenteval.common.PageBaseReq;
import lombok.Data;

import java.util.List;

@Data
public class AgentEvalInstTrackReq extends PageBaseReq {
    private String instanceId;

    private String trackId;

    private List<InstanceTrackDetailInfo> trackDetailInfoList;
}
