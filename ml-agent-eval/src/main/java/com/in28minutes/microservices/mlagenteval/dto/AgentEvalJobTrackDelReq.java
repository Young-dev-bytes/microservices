package com.in28minutes.microservices.mlagenteval.dto;

import lombok.Data;

import java.util.List;

@Data
public class AgentEvalJobTrackDelReq {
    private List<String> jobDeleteList;
}
