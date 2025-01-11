package com.young.microservices.mlagenteval.dto;

import lombok.Data;

import java.util.List;

@Data
public class AgentEvalJobTrackDelReq {

    private List<String> trackIdList;

}
