package com.young.microservices.mlagenteval.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentEvalTaskSaveReq {
    @NotBlank
    private String evalTaskName;

    private String tenantId;

    private String description;

    @Valid
    private List<DeviceInfo> deviceInfoList;

    @NotBlank
    private String timeout;
}
