package com.in28minutes.microservices.mlagenteval.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.in28minutes.microservices.mlagenteval.common.event.JobEventRegisterCenter;
import com.in28minutes.microservices.mlagenteval.common.event.JobExecEvent;
import com.in28minutes.microservices.mlagenteval.constant.Constants;
import com.in28minutes.microservices.mlagenteval.dao.entity.AgentDeviceReferenceDo;
import com.in28minutes.microservices.mlagenteval.dao.entity.AgentEvalJobDo;
import com.in28minutes.microservices.mlagenteval.dao.entity.AgentEvalJobInstanceDo;
import com.in28minutes.microservices.mlagenteval.dao.entity.AgentEvalJobInstanceTrackDo;
import com.in28minutes.microservices.mlagenteval.dao.mapper.*;
import com.in28minutes.microservices.mlagenteval.dto.*;
import com.in28minutes.microservices.mlagenteval.enums.BizErrorCode;
import com.in28minutes.microservices.mlagenteval.exception.BusinessException;
import com.in28minutes.microservices.mlagenteval.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.util.CollectionUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
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

    @Autowired
    private AgentEvalJobInstanceTrackMapper agentEvalJobInstanceTrackMapper;

    /**
     * save online agent job
     *
     * @param request request
     */
    @Transactional(rollbackFor = Exception.class)
    public String saveJob(AgentEvalJobReq request) {
        AgentEvalJobDo agentEvalJobDo = new AgentEvalJobDo();
        BeanUtils.copyProperties(request, agentEvalJobDo);
        agentEvalJobDo.setId(UuidUtils.genSimpleUuid());
        save(agentEvalJobDo);
        return agentEvalJobDo.getId();
    }

    /**
     * updateJob
     *
     * @param request request
     */
    public String updateJob(AgentEvalJobReq request) {
        AgentEvalJobDo agentEvalJobDo = agentEvalJobMapper.selectById(request.getId());
        BeanUtils.copyProperties(request, agentEvalJobDo);
        agentEvalJobMapper.updateById(agentEvalJobDo);
        return agentEvalJobDo.getId();
    }

    /**
     * Obtain job execution records
     *
     * @param request request
     * @return PageBaseResponse
     */
    public PageBaseResponse<AgentEvalJobRes> retrieveJobPage(AgentEvalJobPageReq request) {
        LambdaQueryWrapper<AgentEvalJobDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentEvalJobDo::getTaskId, request.getTaskId()).orderByDesc(AgentEvalJobDo::getUpdateTime);
        if (!request.getCreateUser().isEmpty()) {
            queryWrapper.like(AgentEvalJobDo::getCreateUser, request.getCreateUser());
        }
        Page<AgentEvalJobDo> page = new Page<>(request.getCurPage(), request.getPageSize());
        page = agentEvalJobMapper.selectPage(page, queryWrapper);
        PageBaseResponse<AgentEvalJobRes> jobPageList =
                new PageBaseResponse<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<AgentEvalJobDo> records = page.getRecords();
        List<AgentEvalJobRes> agentEvalJobResList = new ArrayList<>();
        records.forEach(record -> {
            AgentEvalJobRes agentEvalJobRes = new AgentEvalJobRes();
            BeanUtils.copyProperties(record, agentEvalJobRes);
            agentEvalJobResList.add(agentEvalJobRes);
        });

        jobPageList.setRecords(agentEvalJobResList);
        return jobPageList;
    }


    /**
     * retrieve Instance Page
     *
     * @param request request
     * @return PageBaseResponse
     */
    public PageBaseResponse<AgentEvalJobInstanceRes> retrieveInstancePage(AgentEvalJobInstancePageReq request) {
        LambdaQueryWrapper<AgentEvalJobInstanceDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentEvalJobInstanceDo::getJobId, request.getJobId()).orderByDesc(AgentEvalJobInstanceDo::getUpdateTime);
        Page<AgentEvalJobInstanceDo> page = new Page<>(request.getCurPage(), request.getPageSize());
        page = agentEvalJobInstanceMapper.selectPage(page, queryWrapper);
        PageBaseResponse<AgentEvalJobInstanceRes> jobPageList =
                new PageBaseResponse<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<AgentEvalJobInstanceDo> records = page.getRecords();
        List<AgentEvalJobInstanceRes> agentEvalJobInstanceResList = new ArrayList<>();
        records.forEach(record -> {
            AgentEvalJobInstanceRes agentEvalJobInstanceRes = new AgentEvalJobInstanceRes();
            BeanUtils.copyProperties(record, agentEvalJobInstanceRes);
            agentEvalJobInstanceResList.add(agentEvalJobInstanceRes);
        });
        jobPageList.setRecords(agentEvalJobInstanceResList);
        return jobPageList;
    }

    /**
     * job detail
     *
     * @param jobId jobId
     * @return CloudDeviceJobDetailResp
     */
    public AgentEvalJobDetail retrieveJobDetail(String jobId) {
        AgentEvalJobDo evalJobDo = getById(jobId);
        if (Objects.isNull(evalJobDo)) {
            throw new BusinessException(BizErrorCode.SERVER_ERROR, "not found job!");
        }
        LambdaQueryWrapper<AgentDeviceReferenceDo> deviceRefWrapper = new LambdaQueryWrapper<>();
        deviceRefWrapper.eq(AgentDeviceReferenceDo::getTaskId, evalJobDo.getTaskId());

        List<AgentDeviceReferenceDo> referenceDos = agentDeviceRefMapper.selectList(deviceRefWrapper);
        if (CollectionUtils.isEmpty(referenceDos)) {
            throw new BusinessException(BizErrorCode.SERVER_ERROR, "not found devices!");
        }
        List<DeviceInfo> deviceInfoList = new ArrayList<>();
        for (AgentDeviceReferenceDo referenceDo : referenceDos) {
            DeviceInfo deviceInfo = new DeviceInfo();
            BeanUtils.copyProperties(referenceDo, deviceInfo);
            deviceInfoList.add(deviceInfo);
        }

        AgentEvalJobDetail jobDetail = new AgentEvalJobDetail();
        BeanUtils.copyProperties(evalJobDo, jobDetail);
        jobDetail.setDeviceInfoList(deviceInfoList);
        return jobDetail;
    }

    /**
     * exec job
     *
     * @param request request
     */
    public void startExecInstanceJob(AgentEvalJobExecReq request) {
        // find job name, device id, default task status is noStart, execTurn=2,
        AgentEvalJobDetail jobDetail = retrieveJobDetail(request.getJobId());
        if (Objects.isNull(jobDetail)) {
            throw new BusinessException(BizErrorCode.SERVER_ERROR, "not found job!");
        }
        // NfsService nfsService = new NfsService(jobDetail.getNasAddress(), "/");
        // InputStream inputStreamLoad = nfsService.downloadNfsFile(jobDetail.getDatasetPath());
        // jobDetail.setInstanceTaskInfos(UseStreamUtils.readJsonlFile(inputStreamLoad));
        try {
            jobDetail.setInstanceTaskInfos(UseStreamUtils.readJsonlFile(new FileInputStream("/Users/share/Documents/studyfolders/micservic/ml-agent-eval/dataset.jsonl")));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        /*jobDetail.setTenantId(OperatorUtils.getTenantId());
        jobDetail.setProjectId(OperatorUtils.getProjectId());
        jobDetail.setCreateUser(OperatorUtils.getOperator());
        jobDetail.setUpdateUser(OperatorUtils.getOperator());*/
        JobEventRegisterCenter.post(new JobExecEvent(jobDetail));
    }

    /**
     * stop job
     *
     * @param jobId jobId
     */
    public void stopInstanceJob(String jobId) {
        AgentEvalJobInstanceDo agentEvalJobInstanceDo = agentEvalJobInstanceMapper.selectById(jobId);
        agentEvalJobInstanceDo.setJobStatus("STOPPED");
        agentEvalJobInstanceMapper.updateById(agentEvalJobInstanceDo);
    }


    /**
     * jobDelete
     *
     * @param jobId jobId
     */
    @Transactional(rollbackFor = Exception.class)
    public void jobDelete(String jobId) {
        agentEvalJobMapper.deleteById(jobId);
        LambdaQueryWrapper<AgentEvalJobInstanceDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentEvalJobInstanceDo::getJobId, jobId);
        agentEvalJobInstanceMapper.delete(queryWrapper);
    }

    /**
     * jobTrackPage
     *
     * @param agentEvalInstTrackReq req
     * @return List<InstanceTrackInfo>
     */
    public PageBaseResponse<InstanceTrackInfo> jobTrackPage(AgentEvalInstTrackReq agentEvalInstTrackReq) {
        LambdaQueryWrapper<AgentEvalJobInstanceTrackDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentEvalJobInstanceTrackDo::getJobInstanceId, agentEvalInstTrackReq.getInstanceId())
                .orderByDesc(AgentEvalJobInstanceTrackDo::getCreateTime);
        Page<AgentEvalJobInstanceTrackDo> page = new Page<>(agentEvalInstTrackReq.getCurPage(), agentEvalInstTrackReq.getPageSize());
        page = agentEvalJobInstanceTrackMapper.selectPage(page, queryWrapper);
        PageBaseResponse<InstanceTrackInfo> jobPageList =
                new PageBaseResponse<>(page.getCurrent(), page.getSize(), page.getTotal());

        List<AgentEvalJobInstanceTrackDo> jobInstanceTrackDos = page.getRecords();
        List<InstanceTrackInfo> instanceTrackInfoList = new ArrayList<>();
        for (AgentEvalJobInstanceTrackDo jobInstanceTrackDo : jobInstanceTrackDos) {
            String trackInfo = jobInstanceTrackDo.getTrackInfo();
            InstanceTrackInfo instanceTrackInfo = JsonUtils.parseObject(trackInfo, InstanceTrackInfo.class);
            instanceTrackInfo.setTrackId(jobInstanceTrackDo.getId());
            instanceTrackInfoList.add(instanceTrackInfo);
        }
        jobPageList.setRecords(instanceTrackInfoList);
        return jobPageList;
    }

    /**
     * jobTrackDetail
     *
     * @param agentEvalInstTrackReq req
     * @return List<InstanceTrackDetailInfo>
     */
    public List<InstanceTrackDetailInfo> jobTrackDetail(AgentEvalInstTrackReq agentEvalInstTrackReq) {
        LambdaQueryWrapper<AgentEvalJobInstanceTrackDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentEvalJobInstanceTrackDo::getId, agentEvalInstTrackReq.getTrackId())
                .orderByDesc(AgentEvalJobInstanceTrackDo::getCreateTime);
        List<InstanceTrackDetailInfo> instanceTrackDetailInfoList = new ArrayList<>();
        List<AgentEvalJobInstanceTrackDo> jobInstanceTrackDos = agentEvalJobInstanceTrackMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(jobInstanceTrackDos)) {
            AgentEvalJobInstanceTrackDo agentEvalJobInstanceTrackDo = jobInstanceTrackDos.get(0);
            instanceTrackDetailInfoList = JsonUtils.parseList(agentEvalJobInstanceTrackDo.getTrackDetail(), InstanceTrackDetailInfo.class);
        }
        return instanceTrackDetailInfoList;
    }

    /**
     * jobTrackUpdate
     *
     * @param agentEvalInstTrackReq req
     */
    public void jobTrackUpdate(AgentEvalInstTrackReq agentEvalInstTrackReq) {
        AgentEvalJobInstanceTrackDo instanceTrackDo = agentEvalJobInstanceTrackMapper.selectById(agentEvalInstTrackReq.getTrackId());
        if (Objects.isNull(instanceTrackDo)) {
            throw new BusinessException(BizErrorCode.SERVER_ERROR, "not found track");
        }
        List<InstanceTrackDetailInfo> trackDetailInfoList = agentEvalInstTrackReq.getTrackDetailInfoList();
        instanceTrackDo.setTrackDetail(JsonUtils.toJsonString(trackDetailInfoList));
        agentEvalJobInstanceTrackMapper.updateById(instanceTrackDo);
        String trackInfo = instanceTrackDo.getTrackInfo();
        InstanceTrackInfo instanceTrackInfo = JsonUtils.parseObject(trackInfo, InstanceTrackInfo.class);
        for (InstanceTrackDetailInfo trackDetailInfo : trackDetailInfoList) {
            Boolean isSuccess = trackDetailInfo.getIsSuccess();
            if (!isSuccess) {
                instanceTrackInfo.setIsSuccess(false);
                instanceTrackInfo.setTaskProgress("50%");
                instanceTrackDo.setTrackInfo(JsonUtils.toJsonString(instanceTrackInfo));
                agentEvalJobInstanceTrackMapper.updateById(instanceTrackDo);
                break;
            }
        }
    }

    /**
     * checkStepImages
     *
     * @param imgPath imgPath
     */
    public String checkStepImages(String imgPath) {
        InputStream inputStream;
        try {
            inputStream = Files.newInputStream(Paths.get(imgPath.trim()));
            byte[] imageBytes = IOUtils.toByteArray(inputStream);
            return Base64Utils.encodeToString(imageBytes);
        } catch (IOException e) {
            throw new BusinessException(BizErrorCode.SERVER_ERROR, "check images failed.");
        }
    }

    /**
     * trackBatchDelete
     *
     * @param request req
     */
    public void trackBatchDelete(AgentEvalJobTrackDelReq request) {
        if (CollectionUtils.isEmpty(request.getJobDeleteList())) {
            return;
        }
        agentEvalJobInstanceTrackMapper.deleteBatchIds(request.getJobDeleteList());
    }

    /**
     * trackDownload
     *
     * @param request req
     * @return ResponseEntity
     * @throws IOException IOException
     */
    public ResponseEntity<InputStream> trackDownload(AgentEvalJobTrackDownReq request) throws IOException {
        List<String> trackIdList = request.getTrackIdList();
        String instanceId = request.getInstanceId();
        AgentEvalJobInstanceDo agentEvalJobInstanceDo = agentEvalJobInstanceMapper.selectById(instanceId);
        if (Objects.isNull(agentEvalJobInstanceDo)) {
            throw new BusinessException(BizErrorCode.SERVER_ERROR, "instance not found");
        }
        String jobId = agentEvalJobInstanceDo.getJobId();
        String sourceFoldPath;
        String zipFilePath;
        String system = System.getProperty("os.name").toLowerCase();
        String zipFileName = instanceId.concat(".zip");
        if (system.contains("win")) {
            // window test files
            sourceFoldPath = FilePathUtils.buildFilePath("D:/work/agent-simulation/agent_eval/ui_agent/", jobId, instanceId);
            zipFilePath = FilePathUtils.buildFilePath("D:/work/agent-simulation/agent_eval/ui_agent/", jobId, zipFileName);
        } else {
            // linux test files
            sourceFoldPath = FilePathUtils.buildFilePath(Constants.IMG_ROOT_PATH, jobId, instanceId);
            zipFilePath = FilePathUtils.buildFilePath(Constants.IMG_ROOT_PATH, jobId, zipFileName);
        }
        ZipUtils.zipMultiFiles(sourceFoldPath, zipFilePath, trackIdList);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + zipFileName)
                .body(Files.newInputStream(Paths.get(zipFilePath)));
    }

    /*public ResponseEntity<InputStream> trackDownload(AgentEvalJobTrackDownReq request) throws IOException {
        List<String> trackIdList = request.getTrackIdList();
        String instanceId = request.getInstanceId();
        AgentEvalJobInstanceDo agentEvalJobInstanceDo = agentEvalJobInstanceMapper.selectById(instanceId);
        if (Objects.isNull(agentEvalJobInstanceDo)) {
            throw new BusinessException(BizErrorCode.SERVER_ERROR, "instance not found");
        }
        NfsService nfsService = new NfsService(Constants.NFS_SERVER, Constants.EXPORTED_PATH);
        String jobId = agentEvalJobInstanceDo.getJobId();
        String dirPath = "D:/work/agent-simulation/agent_eval/ui_agent/" + jobId + "/" + instanceId;
        // String dirPath = Constants.WORKING_DIR + jobId + Constants.WORKING_DIR + instanceId;
        String zipFileName = "D:/work/agent-simulation/agent_eval/ui_agent/" + jobId + "/" + instanceId.concat(".zip");

        FileInputStream fileInputStream = new FileInputStream(dirPath);

        *//*try {
            nfsService.zipDirectory(dirPath, zipFileName);
        } catch (IOException e) {
            throw new BusinessException(BizErrorCode.SERVER_ERROR, "Image collection file compression failed");
        }*//*

        // InputStream inputStream = nfsService.downloadNfsFile(zipFileName);
        *//*if (fileInputStream == null) {
            throw new BusinessException(BizErrorCode.SERVER_ERROR, "The downloaded file stream is empty");
        }*//*
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + instanceId.concat(".zip"))
                .body(fileInputStream);
    }*/
}
