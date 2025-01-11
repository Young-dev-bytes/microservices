package com.young.microservices.mlagenteval.dto;

import com.young.microservices.mlagenteval.dao.entity.AgentEvalTaskDo;
import lombok.Data;

import java.util.List;

@Data
public class AgentEvalTaskDetail extends AgentEvalTaskDo {
    private static final long serialVersionUID = 624898579952076753L;

    private Boolean isExistsJob;

    private List<DeviceInfo> deviceInfoList;
}
