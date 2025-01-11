package com.young.microservices.mlagenteval.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class AgentRelOccReq {
    @NotBlank
    private String taskId;

    @Valid
    private List<DeviceInfo> deviceInfoList;

    private String timeout;
}
