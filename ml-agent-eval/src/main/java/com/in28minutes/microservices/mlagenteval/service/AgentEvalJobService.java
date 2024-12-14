package com.in28minutes.microservices.mlagenteval.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.in28minutes.microservices.mlagenteval.common.event.JobEventRegisterCenter;
import com.in28minutes.microservices.mlagenteval.common.event.JobExecEvent;
import com.in28minutes.microservices.mlagenteval.dao.entity.AgentDeviceReferenceDo;
import com.in28minutes.microservices.mlagenteval.dao.entity.AgentEvalJobDo;
import com.in28minutes.microservices.mlagenteval.dao.entity.AgentEvalJobInstanceDo;
import com.in28minutes.microservices.mlagenteval.dao.entity.AgentEvalJobInstanceTrackDo;
import com.in28minutes.microservices.mlagenteval.dao.mapper.*;
import com.in28minutes.microservices.mlagenteval.dto.*;
import com.in28minutes.microservices.mlagenteval.enums.BizErrorCode;
import com.in28minutes.microservices.mlagenteval.exception.BusinessException;
import com.in28minutes.microservices.mlagenteval.utils.JsonUtils;
import com.in28minutes.microservices.mlagenteval.utils.UuidUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
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
     * @return String
     */
    @Transactional(rollbackFor = Exception.class)
    public void save(AgentEvalJobSaveReq request) {
        AgentEvalJobDo agentEvalJobDo = new AgentEvalJobDo();
        BeanUtils.copyProperties(request, agentEvalJobDo);
        agentEvalJobDo.setId(UuidUtils.genSimpleUuid());
        save(agentEvalJobDo);
    }


    /**
     * Obtain job execution records
     *
     * @param request request
     * @return PageBaseResponse
     */
    public PageBaseResponse getJobListByTask(AgentEvalJobPageReq request) {
        /*LambdaQueryWrapper<AgentEvalJobDo> queryWrapper = new LambdaQueryWrapper<>();
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
    public void startExecJob(AgentEvalJobExecReq request) {

        // find job name, device id, default task status is noStart, execTurn=2,
        AgentEvalJobDetail jobDetail = getJobDetail(request.getJobId());
        if (Objects.isNull(jobDetail)) {
            throw new BusinessException(BizErrorCode.SERVER_ERROR, "not found job!");
        }

        // NfsService nfsService = new NfsService(jobDetail.getNasAddress(), "/");
        // InputStream inputStreamLoad = nfsService.downloadNfsFile(jobDetail.getDatasetPath());

        /*jobDetail.setTenantId(OperatorUtils.getTenantId());
        jobDetail.setProjectId(OperatorUtils.getProjectId());
        jobDetail.setCreateUser(OperatorUtils.getOperator());
        jobDetail.setUpdateUser(OperatorUtils.getOperator());*/
        String path = "/Users/share/Documents/studyfolders/micservic/ml-agent-eval/dataset.jsonl";
        jobDetail.setInstanceTaskInfos(readJsonlFile(path));
        JobEventRegisterCenter.post(new JobExecEvent(jobDetail));
    }

    @Transactional(rollbackFor = Exception.class)
    public void jobDelete(String jobId) {
        agentEvalJobMapper.deleteById(jobId);
        LambdaQueryWrapper<AgentEvalJobInstanceDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentEvalJobInstanceDo::getJobId, jobId);
        agentEvalJobInstanceMapper.delete(queryWrapper);
    }

    public static List<InstanceTaskInfo> readJsonlFile(String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<InstanceTaskInfo> instanceTaskInfos = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                InstanceTaskInfo taskInfo = objectMapper.readValue(line, InstanceTaskInfo.class);
                instanceTaskInfos.add(taskInfo);
            }
        } catch (IOException e) {
            log.error("read Json line File error.");
            throw new BusinessException(BizErrorCode.SERVER_ERROR, "read Json line File error.");
        }
        return instanceTaskInfos;
    }

    public List<InstanceTrackInfo> jobTrackPage(AgentEvalInstTrackReq agentEvalInstTrackReq) {
        LambdaQueryWrapper<AgentEvalJobInstanceTrackDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentEvalJobInstanceTrackDo::getJobInstanceId, agentEvalInstTrackReq.getInstanceId())
                .orderByDesc(AgentEvalJobInstanceTrackDo::getCreateTime);

        List<AgentEvalJobInstanceTrackDo> jobInstanceTrackDos = agentEvalJobInstanceTrackMapper.selectList(queryWrapper);
        List<InstanceTrackInfo> instanceTrackInfoList = new ArrayList<>();
        for (AgentEvalJobInstanceTrackDo jobInstanceTrackDo : jobInstanceTrackDos) {
            String trackInfo = jobInstanceTrackDo.getTrackInfo();
            InstanceTrackInfo instanceTrackInfo = JsonUtils.parseObject(trackInfo, InstanceTrackInfo.class);
            instanceTrackInfo.setTrackId(jobInstanceTrackDo.getId());
            instanceTrackInfoList.add(instanceTrackInfo);
        }
        return instanceTrackInfoList;
    }

    public List<InstanceTrackDetailInfo> jobTrackDetail(AgentEvalInstTrackReq agentEvalInstTrackReq) {
        LambdaQueryWrapper<AgentEvalJobInstanceTrackDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentEvalJobInstanceTrackDo::getId, agentEvalInstTrackReq.getTrackId())
                .orderByDesc(AgentEvalJobInstanceTrackDo::getCreateTime);
        List<InstanceTrackDetailInfo> instanceTrackDetailInfoList = new ArrayList<>();
        List<AgentEvalJobInstanceTrackDo> jobInstanceTrackDos = agentEvalJobInstanceTrackMapper.selectList(queryWrapper);
        if(!CollectionUtils.isEmpty(jobInstanceTrackDos)) {
            AgentEvalJobInstanceTrackDo agentEvalJobInstanceTrackDo = jobInstanceTrackDos.get(0);
            instanceTrackDetailInfoList = JsonUtils.parseList(agentEvalJobInstanceTrackDo.getTrackDetail(), InstanceTrackDetailInfo.class);
        }
        return instanceTrackDetailInfoList;
    }

    public void jobTrackUpdate(AgentEvalInstTrackReq agentEvalInstTrackReq) {
        AgentEvalJobInstanceTrackDo instanceTrackDo = agentEvalJobInstanceTrackMapper.selectById(agentEvalInstTrackReq.getTrackId());
        if(Objects.isNull(instanceTrackDo)) {
            throw new BusinessException(BizErrorCode.SERVER_ERROR, "not found track");
        }
        List<InstanceTrackDetailInfo> trackDetailInfoList = agentEvalInstTrackReq.getTrackDetailInfoList();
        instanceTrackDo.setTrackDetail(JsonUtils.toJsonString(trackDetailInfoList));
        agentEvalJobInstanceTrackMapper.updateById(instanceTrackDo);
        String trackInfo = instanceTrackDo.getTrackInfo();
        InstanceTrackInfo instanceTrackInfo = JsonUtils.parseObject(trackInfo, InstanceTrackInfo.class);
        for (InstanceTrackDetailInfo trackDetailInfo : trackDetailInfoList) {
            Boolean isSuccess = trackDetailInfo.getIsSuccess();
            if(!isSuccess) {
                instanceTrackInfo.setIsSuccess(false);
                instanceTrackInfo.setTaskProgress("50%");
                instanceTrackDo.setTrackInfo(JsonUtils.toJsonString(instanceTrackInfo));
                agentEvalJobInstanceTrackMapper.updateById(instanceTrackDo);
                break;
            }
        }
    }

    public void checkStepImages(String imgPath) {

        Path imagePath = Paths.get(imgPath);

        if (!Files.exists(imagePath)) {

        }

        try {
            File imageFile = imagePath.toFile();
            InputStreamResource resource = new InputStreamResource(new FileInputStream(imageFile));

            // 获取MIME类型
            String contentType = Files.probeContentType(imagePath);
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + imageFile.getName());
            headers.setContentType(MediaType.parseMediaType(contentType));

            // 返回响应实体
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(imageFile.length())
                    .body(resource);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String sanitizeFilename(String filename) {
        return filename.replaceAll("[^a-zA-Z0-9.-]", "");
    }
}
