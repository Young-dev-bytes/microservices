package com.young.microservices.mlagenteval.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class InstanceTaskInfo {

    @JsonProperty("scene")
    private String scene;

    @JsonProperty("app")
    private String app;

    @JsonProperty("instruction")
    private String instruction;

    @JsonProperty("dag")
    private String[] dag;

    @JsonProperty("step_num")
    private String stepNum;
}
