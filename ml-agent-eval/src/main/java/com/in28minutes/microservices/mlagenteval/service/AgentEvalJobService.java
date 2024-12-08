package com.in28minutes.microservices.mlagenteval.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.in28minutes.microservices.mlagenteval.common.event.JobEventRegisterCenter;
import com.in28minutes.microservices.mlagenteval.common.event.JobExecEvent;
import com.in28minutes.microservices.mlagenteval.dao.entity.AgentDeviceReferenceDo;
import com.in28minutes.microservices.mlagenteval.dao.entity.AgentEvalJobDo;
import com.in28minutes.microservices.mlagenteval.dao.entity.AgentEvalJobInstanceDo;
import com.in28minutes.microservices.mlagenteval.dao.mapper.AgentDeviceReferenceMapper;
import com.in28minutes.microservices.mlagenteval.dao.mapper.AgentEvalJobInstanceMapper;
import com.in28minutes.microservices.mlagenteval.dao.mapper.AgentEvalJobMapper;
import com.in28minutes.microservices.mlagenteval.dao.mapper.AgentEvalTaskMapper;
import com.in28minutes.microservices.mlagenteval.dto.*;
import com.in28minutes.microservices.mlagenteval.utils.UuidUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Service
public class AgentEvalJobService extends ServiceImpl<AgentEvalJobMapper, AgentEvalJobDo> {

    @Autowired
    private AgentEvalTaskMapper agentEvalTaskMapper;

    @Autowired
    private AgentEvalJobMapper agentEvalJobMapper;

    @Autowired
    private AgentDeviceReferenceMapper agentDeviceRefMapper;

    @Autowired
    private AgentEvalJobInstanceMapper agentEvalJobInstanceMapper;


    /**
     * save online agent job
     *
     * @param request request
     * @return String
     */
    @Transactional(rollbackFor = Exception.class)
    public void save(AgentEvalJobSaveReq request) {
        AgentEvalJobDo agentEvalJobDo = new AgentEvalJobDo();
        BeanUtils.copyProperties(request, agentEvalJobDo);
        save(agentEvalJobDo);
    }


    /**
     * Obtain job execution records
     *
     * @param request request
     * @return PageBaseResponse
     */
    public Object getJobListByTask(AgentEvalJobPageReq request) {
        /* LambdaQueryWrapper<AgentEvalJobDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentEvalJobDo::getTaskId, request.getTaskId()).orderByDesc(AgentEvalJobDo::getUpdateTime);
        if (!request.getCreateUser().isEmpty()) {
            queryWrapper.like(AgentEvalJobDo::getCreateUser, request.getCreateUser());
        }
        Page<AgentEvalJobDo> page = new Page<>(request.getCurPage(), request.getPageSize());
        page = agentEvalJobMapper.selectPage(page, queryWrapper);
        PageBaseResponse<AgentEvalJobDo> jobPageList =
                new PageBaseResponse<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<AgentEvalJobDo> records = page.getRecords();
        jobPageList.setRecords(records);
        return jobPageList;*/
        return null;
    }


    /**
     * job detail
     *
     * @param jobId jobId
     * @return CloudDeviceJobDetailResp
     */
    public AgentEvalJobDetail getJobDetail(String jobId) {
        AgentEvalJobDo evalJobDo = getById(jobId);
        if (Objects.isNull(evalJobDo)) {
            throw new RuntimeException("error");
        }
        LambdaQueryWrapper<AgentDeviceReferenceDo> deviceRefWrapper = new LambdaQueryWrapper<>();
        deviceRefWrapper.eq(AgentDeviceReferenceDo::getTaskId, evalJobDo.getTaskId());

        List<AgentDeviceReferenceDo> referenceDos = agentDeviceRefMapper.selectList(deviceRefWrapper);
        if (CollectionUtils.isEmpty(referenceDos)) {
            throw new RuntimeException("error");
        }
        List<DeviceInfo> deviceInfoList = null;
        for (AgentDeviceReferenceDo referenceDo : referenceDos) {
            DeviceInfo deviceInfo = new DeviceInfo();
            BeanUtils.copyProperties(referenceDo, deviceInfo);
            deviceInfoList.add(deviceInfo);
        }

        AgentEvalJobDetail jobDetail = new AgentEvalJobDetail();
        BeanUtils.copyProperties(jobDetail, evalJobDo);
        jobDetail.setDeviceInfoList(deviceInfoList);
        return jobDetail;
    }

    /**
     * exec job
     *
     * @param request request
     */
    public void startExecJob(AgentEvalJobExecReq request) {

        // find job name, device id, default task status is noStart, execTurn=2,
        AgentEvalJobDetail jobDetail = getJobDetail(request.getJobId());
        if (Objects.isNull(jobDetail)) {
            throw new RuntimeException("error");
        }

        AgentEvalJobInstanceDo jobInstanceDo = new AgentEvalJobInstanceDo();
        jobInstanceDo.setId(UuidUtils.genSimpleUuid());
        jobInstanceDo.setJobStatus("created");
        jobInstanceDo.setJobId(jobDetail.getId());
        jobInstanceDo.setTurn(jobDetail.getExecuteTurn());
        jobInstanceDo.setTrackInfo("");
        agentEvalJobInstanceMapper.insert(jobInstanceDo);
        DeviceInfo deviceInfo = jobDetail.getDeviceInfoList().get(0);

        JobEventRegisterCenter.post(new JobExecEvent(jobInstanceDo.getId(),deviceInfo));
    }
}
