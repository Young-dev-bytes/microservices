package com.in28minutes.microservices.mlagenteval.common.event.handler;

import com.google.common.eventbus.Subscribe;
import com.in28minutes.microservices.mlagenteval.common.event.JobStatusEvent;
import com.in28minutes.microservices.mlagenteval.dao.entity.AgentEvalJobInstanceDo;
import com.in28minutes.microservices.mlagenteval.dao.mapper.AgentEvalJobInstanceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

/**
 * desc:
 *
 * @author Young.
 * @since 2024/5/18 17:58
 */
@Slf4j
public class JobStatusEventHandler {
    @Autowired
    private AgentEvalJobInstanceMapper instanceMapper;

    @Subscribe
    public void handle(JobStatusEvent jobStatusEvent) {
        log.info("jobStatusEvent: instanceId[{}], status[{}]", jobStatusEvent.getInstanceId(), jobStatusEvent.getStatus());
        AgentEvalJobInstanceDo evalJobInstanceDo = instanceMapper.selectById(jobStatusEvent.getInstanceId());
        if (Objects.isNull(evalJobInstanceDo)) {
            throw new RuntimeException("instance not exist!");
        }
        evalJobInstanceDo.setJobStatus(jobStatusEvent.getStatus());
        instanceMapper.updateById(evalJobInstanceDo);
    }
}
