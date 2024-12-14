package com.in28minutes.microservices.mlagenteval.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * desc:
 *
 * @author Young.
 * @since 2024/5/18 17:59
 */
@Data
@AllArgsConstructor
public class JobStatusEvent {
    private String instanceId;

    private String status;

    private Integer currentTurn;

    private String errorInfo;
}
