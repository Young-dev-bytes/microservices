package com.young.microservices.mlagenteval.dto;

import lombok.Data;

import java.util.List;

@Data
public class AgentEvalTaskDeleteReq {
    private List<String> taskDeleteList;

}
