package com.young.microservices.mlagenteval.dto;

import com.young.microservices.mlagenteval.dao.entity.AgentEvalJobDo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgentEvalJobDetail extends AgentEvalJobDo {
    private List<DeviceInfo> deviceInfoList;

    private List<InstanceTaskInfo> instanceTaskInfos;

    private String nasAddress;

    private String datasetPath;

    private TenantNasInfo tenantNasInfo;

    private String tenantCode;

    public String getParams() {
        return null;
    }
}
