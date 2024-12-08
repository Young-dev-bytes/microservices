package com.in28minutes.microservices.mlagenteval.dto;

import com.in28minutes.microservices.mlagenteval.dao.entity.AgentEvalJobDo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgentEvalJobDetail extends AgentEvalJobDo {

    private List<DeviceInfo> deviceInfoList;

}
