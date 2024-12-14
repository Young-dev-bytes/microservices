package com.in28minutes.microservices.mlagenteval.common.event;

import com.in28minutes.microservices.mlagenteval.dto.AgentEvalJobDetail;
import com.in28minutes.microservices.mlagenteval.dto.DeviceInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * desc:
 *
 * @author Young.
 * @since 2024/5/18 17:35
 */
@Data
@AllArgsConstructor
public class JobExecEvent {
    private String jobId;

    private DeviceInfo deviceInfo;

    private Integer executeTurn;

    private AgentEvalJobDetail jobDetail;

    public JobExecEvent(AgentEvalJobDetail agentEvalJobDetail) {
        this.jobDetail = agentEvalJobDetail;
    }
}
