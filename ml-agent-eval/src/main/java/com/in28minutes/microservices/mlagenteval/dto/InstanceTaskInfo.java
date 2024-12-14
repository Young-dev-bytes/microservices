package com.in28minutes.microservices.mlagenteval.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InstanceTaskInfo {
    @JsonProperty
    private String scene;
    @JsonProperty
    private String app;
    @JsonProperty
    private String dag;
    @JsonProperty
    private String instruction;
    @JsonProperty("step_num")
    private String stepNum;
}
