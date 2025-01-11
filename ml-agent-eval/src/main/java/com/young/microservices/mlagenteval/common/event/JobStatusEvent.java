package com.young.microservices.mlagenteval.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

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

    private LocalDateTime endTime;

    private String errorInfo;
}
