package com.young.microservices.mlagenteval.common.event.handler;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.eventbus.Subscribe;
import com.young.microservices.mlagenteval.common.event.JobTrackDetailEvent;
import com.young.microservices.mlagenteval.dao.entity.AgentEvalJobInstanceTrackDo;
import com.young.microservices.mlagenteval.dao.mapper.AgentEvalJobInstanceTrackMapper;
import com.young.microservices.mlagenteval.dto.InstanceTrackDetailInfo;
import com.young.microservices.mlagenteval.enums.BizErrorCode;
import com.young.microservices.mlagenteval.exception.BusinessException;
import com.young.microservices.mlagenteval.utils.JsonUtils;
import com.young.microservices.mlagenteval.utils.spring.SpringBeanUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class JobTrackDetailEventHandler {
    @Subscribe
    public void handle(JobTrackDetailEvent jobTrackDetailEvent) {
        AgentEvalJobInstanceTrackMapper agentEvalJobInstanceTrackMapper = SpringBeanUtils.getBean(AgentEvalJobInstanceTrackMapper.class);
        log.info("jobTrackDetailEvent: trackId[{}]", jobTrackDetailEvent.getTrackId());
        AgentEvalJobInstanceTrackDo evalJobInstanceDo = agentEvalJobInstanceTrackMapper.selectById(jobTrackDetailEvent.getTrackId());
        if (Objects.isNull(evalJobInstanceDo)) {
            throw new BusinessException(BizErrorCode.SERVER_ERROR, "track info not exist!");
        }
        List<String> respInfers = jobTrackDetailEvent.getRespInfers();
        if (!CollectionUtils.isEmpty(respInfers)) {
            List<InstanceTrackDetailInfo> instanceTrackDetailInfos =
                    parseRespInferToInstanceTrackDetailInfo(respInfers);
            evalJobInstanceDo.setTrackDetail(JsonUtils.toJsonString(instanceTrackDetailInfos));
        }
        agentEvalJobInstanceTrackMapper.updateById(evalJobInstanceDo);
    }

    private static List<InstanceTrackDetailInfo> parseRespInferToInstanceTrackDetailInfo(List<String> respInfers) {
        List<InstanceTrackDetailInfo> detailInfoList = new ArrayList<>();
        for (String respInfer : respInfers) {
            InstanceTrackDetailInfo detailInfo = new InstanceTrackDetailInfo();
            String[] parts = respInfer.split("\\n+");
            for (String part : parts) {
                if (part.startsWith("Observation: ")) {
                    detailInfo.setObservation(part.substring("Observation: ".length()));
                } else if (part.startsWith("Thought: ")) {
                    detailInfo.setThought(part.substring("Thought: ".length()));
                } else if (part.startsWith("Action: ")) {
                    detailInfo.setAction(part.substring("Action: ".length()));
                } else if (part.startsWith("Step: ")) {
                    detailInfo.setStep(part.substring("Step: ".length()));
                } else if (part.startsWith("ImgPathBefore: ")) {
                    detailInfo.setImagePathBefore(part.substring("ImgPathBefore: ".length()));
                } else if (part.startsWith("ImgPathAfter: ")) {
                    detailInfo.setImagePathAfter(part.substring("ImgPathAfter: ".length()));
                }
            }
            detailInfoList.add(detailInfo);
        }
        return detailInfoList;
    }
}
