package com.young.microservices.mlagenteval.common.event.handler;

import com.google.common.base.Strings;
import com.google.common.eventbus.Subscribe;
import com.young.microservices.mlagenteval.common.event.JobStatusEvent;
import com.young.microservices.mlagenteval.dao.entity.AgentEvalJobInstanceDo;
import com.young.microservices.mlagenteval.dao.mapper.AgentEvalJobInstanceMapper;
import com.young.microservices.mlagenteval.enums.BizErrorCode;
import com.young.microservices.mlagenteval.exception.BusinessException;
import com.young.microservices.mlagenteval.utils.spring.SpringBeanUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * desc:
 *
 * @author Young.
 * @since 2024/5/18 17:58
 */
@Slf4j
public class JobStatusEventHandler {

    @Subscribe
    public void handle(JobStatusEvent jobStatusEvent) {
        AgentEvalJobInstanceMapper instanceMapper = SpringBeanUtils.getBean(AgentEvalJobInstanceMapper.class);
        log.info("jobStatusEvent: instanceId[{}], status[{}], current turn[{}]", jobStatusEvent.getInstanceId(), jobStatusEvent.getStatus(), jobStatusEvent.getCurrentTurn());
        AgentEvalJobInstanceDo evalJobInstanceDo = instanceMapper.selectById(jobStatusEvent.getInstanceId());
        if (Objects.isNull(evalJobInstanceDo)) {
            throw new BusinessException(BizErrorCode.SERVER_ERROR, "instance not exist!");
        }
        evalJobInstanceDo.setJobStatus(jobStatusEvent.getStatus());
        if(!Objects.isNull(jobStatusEvent.getCurrentTurn())) {
            evalJobInstanceDo.setCurrentTurn(jobStatusEvent.getCurrentTurn());
        }
        if(!Strings.isNullOrEmpty(jobStatusEvent.getErrorInfo())) {
            evalJobInstanceDo.setErrorInfo(jobStatusEvent.getErrorInfo());
        }
        if(!Objects.isNull(jobStatusEvent.getEndTime())) {
            evalJobInstanceDo.setEndTime(jobStatusEvent.getEndTime());
        }
        instanceMapper.updateById(evalJobInstanceDo);
    }
}
