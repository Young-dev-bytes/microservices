package com.in28minutes.microservices.mlagenteval.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class AgentEvalTaskSaveReq {

    @NotBlank
    private String evalTaskName;
    @NotBlank
    private String evalTaskType;

    @Valid
    private List<DeviceInfo> deviceInfoList;
    @NotBlank
    private String timeout;
}
