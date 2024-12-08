package com.in28minutes.microservices.mlagenteval.dto;

import com.in28minutes.microservices.mlagenteval.dao.entity.AgentEvalTaskDo;
import lombok.Data;

import java.util.List;

@Data
public class AgentEvalTaskDetail extends AgentEvalTaskDo {
    private Boolean isExistsJob;

    private List<DeviceInfo> deviceInfoList;

    private String model;
}
