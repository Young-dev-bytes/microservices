package com.young.microservices.mlagenteval.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.young.microservices.mlagenteval.common.CommonResponse;
import com.young.microservices.mlagenteval.common.cache.DeviceInfoCache;
import com.young.microservices.mlagenteval.common.event.JobEventRegisterCenter;
import com.young.microservices.mlagenteval.common.event.JobExecEvent;
import com.young.microservices.mlagenteval.constant.Constants;
import com.young.microservices.mlagenteval.dao.entity.AgentDeviceReferenceDo;
import com.young.microservices.mlagenteval.dao.entity.AgentEvalJobDo;
import com.young.microservices.mlagenteval.dao.entity.AgentEvalJobInstanceDo;
import com.young.microservices.mlagenteval.dao.entity.AgentEvalJobInstanceTrackDo;
import com.young.microservices.mlagenteval.dao.mapper.*;
import com.young.microservices.mlagenteval.dto.*;
import com.young.microservices.mlagenteval.enums.BizErrorCode;
import com.young.microservices.mlagenteval.enums.JobStatusEnum;
import com.young.microservices.mlagenteval.exception.BusinessException;
import com.young.microservices.mlagenteval.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AgentEvalJobService extends ServiceImpl<AgentEvalJobMapper, AgentEvalJobDo> {
    @Autowired
    private AgentEvalTaskMapper agentEvalTaskMapper;

    @Autowired
    private AgentEvalTaskService agentEvalTaskService;

    @Autowired
    private AgentEvalJobMapper agentEvalJobMapper;

    @Autowired
    private AgentDeviceReferenceMapper agentDeviceRefMapper;

    @Autowired
    private AgentEvalJobInstanceMapper agentEvalJobInstanceMapper;

    @Autowired
    private AgentEvalJobInstanceTrackMapper agentEvalJobInstanceTrackMapper;

    @Autowired
    private DeviceInfoCache deviceInfoCache;

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
        AgentEvalJobDo agentEvalJobDo = getById(request.getJobId());
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
            agentEvalJobInstanceRes.setJobStatusDesc(JobStatusEnum.getByValue(record.getJobStatus()));
            agentEvalJobInstanceRes.setStartTime(record.getCreateTime());
            agentEvalJobInstanceRes.setExecuteUser(record.getCreateUser());
            agentEvalJobInstanceRes.setJobName(agentEvalJobDo.getJobName());
            agentEvalJobInstanceRes.setDeviceName(agentEvalTaskService.getDeviceFullName(record.getDeviceId()));
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
            deviceInfo.setDeviceName(agentEvalTaskService.getDeviceFullName(referenceDo.getDeviceUdid()));
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
        TenantNasInfo tenantNasInfo = agentEvalTaskService.checkNasInfos();
        NfsService nfsService = new NfsService(jobDetail.getNasAddress(), Constants.WORKING_DIR);
        InputStream inputStreamLoad = nfsService.downloadNfsFile(jobDetail.getDatasetPath());
        jobDetail.setInstanceTaskInfos(UseStreamUtils.readJsonlFile(inputStreamLoad));
        jobDetail.setTenantNasInfo(tenantNasInfo);
        jobDetail.setTenantCode(OperatorUtils.getNamespace());
        jobDetail.setTenantId(OperatorUtils.getTenantId());
        jobDetail.setProjectId(OperatorUtils.getProjectId());
        jobDetail.setCreateUser(OperatorUtils.getOperator());
        jobDetail.setUpdateUser(OperatorUtils.getOperator());
        JobEventRegisterCenter.post(new JobExecEvent(jobDetail));
    }

    /**
     * stop job
     *
     * @param jobId jobId
     */
    public void stopInstanceJob(String jobId) {
        AgentEvalJobInstanceDo agentEvalJobInstanceDo = agentEvalJobInstanceMapper.selectById(jobId);
        agentEvalJobInstanceDo.setJobStatus(JobStatusEnum.SHUTDOWN.getValue());
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
            instanceTrackInfo.setTargetStepNum(calculateTargetStep(jobInstanceTrackDo.getTrackInfo()));
            String trackDetail = jobInstanceTrackDo.getTrackDetail();
            instanceTrackInfo.setRealStepNum(Strings.isEmpty(trackDetail) ? 0 : JsonUtils.parseList(trackDetail, InstanceTrackDetailInfo.class).size());
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
            String trackDetail = agentEvalJobInstanceTrackDo.getTrackDetail();
            if(!Strings.isEmpty(trackDetail)) {
                instanceTrackDetailInfoList = JsonUtils.parseList(trackDetail, InstanceTrackDetailInfo.class);
            }
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
        InstanceTrackInfo instanceTrackInfo = JsonUtils.parseObject(instanceTrackDo.getTrackInfo(), InstanceTrackInfo.class);
        instanceTrackInfo.setIsSuccess(trackDetailInfoList.get(trackDetailInfoList.size() - 1).getIsSuccess());
        calculateTaskProgress(trackDetailInfoList, instanceTrackInfo);
        instanceTrackDo.setTrackInfo(JsonUtils.toJsonString(instanceTrackInfo));
        agentEvalJobInstanceTrackMapper.updateById(instanceTrackDo);
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
        if (CollectionUtils.isEmpty(request.getTrackIdList())) {
            return;
        }
        agentEvalJobInstanceTrackMapper.deleteBatchIds(request.getTrackIdList());
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
        TenantNasInfo tenantNasInfo = agentEvalTaskService.checkNasInfos();

        List<TrackExportDetail> trackExportDetails = buildTrackExportDetails(trackIdList, agentEvalJobInstanceDo);
        exportTrackDetails(trackExportDetails, agentEvalJobInstanceDo, tenantNasInfo);
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
            sourceFoldPath = FilePathUtils.buildFilePath(Constants.IMG_ROOT_PATH, tenantNasInfo.getNasDir(), OperatorUtils.getNamespace(), jobId, instanceId);
            zipFilePath = FilePathUtils.buildFilePath(Constants.IMG_ROOT_PATH, tenantNasInfo.getNasDir(), OperatorUtils.getNamespace(), jobId, zipFileName);
        }
        log.info("sourceFoldPath:{}, zipFilePath:{}", sourceFoldPath, zipFilePath);
        ZipUtils.zipMultiFiles(sourceFoldPath, zipFilePath, trackIdList);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + zipFileName)
                .body(Files.newInputStream(Paths.get(zipFilePath)));
    }

    /**
     * track analyze
     *
     * @param request req
     * @return
     */
    public TaskIndicatorRes trackAnalyze(AgentEvalJobTrackDownReq request) {
        TaskIndicatorRes taskIndicatorRes = new TaskIndicatorRes();
        List<TrackIndicatorInfo> indicatorInfoList = new ArrayList<>();
        LambdaQueryWrapper<AgentEvalJobInstanceTrackDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentEvalJobInstanceTrackDo::getJobInstanceId, request.getInstanceId())
                .orderByDesc(AgentEvalJobInstanceTrackDo::getCreateTime);
        List<AgentEvalJobInstanceTrackDo> agentEvalJobInstanceTrackDos = agentEvalJobInstanceTrackMapper.selectList(queryWrapper);
        for (AgentEvalJobInstanceTrackDo agentEvalJobInstanceTrackDo : agentEvalJobInstanceTrackDos) {
            TrackIndicatorInfo trackIndicatorInfo = new TrackIndicatorInfo();
            InstanceTrackInfo instanceTrackInfo = JsonUtils.parseObject(agentEvalJobInstanceTrackDo.getTrackInfo(), InstanceTrackInfo.class);
            if (Objects.isNull(instanceTrackInfo.getIsSuccess()) || Strings.isEmpty(instanceTrackInfo.getTaskProgress())) {
                throw new BusinessException(BizErrorCode.SERVER_ERROR, "Please analyze the evaluation results");
            }
            trackIndicatorInfo.setScene(instanceTrackInfo.getScene());
            trackIndicatorInfo.setTaskSuccessRate(instanceTrackInfo.getIsSuccess() ? "100%" : "0%");
            trackIndicatorInfo.setTaskCompletion(instanceTrackInfo.getTaskProgress());
            indicatorInfoList.add(trackIndicatorInfo);
        }
        taskIndicatorRes.setTotalTaskSuccessRate(String.format("%d%%", (int) indicatorInfoList.stream()
                .mapToDouble(info -> "100%".equals(info.getTaskSuccessRate()) ? 100 : 0)
                .average()
                .orElse(0)));
        taskIndicatorRes.setTotalTaskCompletion(String.format("%d%%", (int) indicatorInfoList.stream()
                .mapToDouble(info -> Double.parseDouble(info.getTaskCompletion().replace("%", "")))
                .average()
                .orElse(0)));
        taskIndicatorRes.setIndicatorInfoList(indicatorInfoList);
        return taskIndicatorRes;
    }


    /**
     * track Analyze DownLoad
     *
     * @param request req
     * @return ResponseEntity
     * @throws IOException
     */
    public ResponseEntity<InputStream> trackAnalyzeDownLoad(AgentEvalJobTrackDownReq request) throws IOException {
        TaskIndicatorRes taskIndicatorRes = trackAnalyze(request);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Task Indicators");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("场景");
        headerRow.createCell(1).setCellValue("任务成功率");
        headerRow.createCell(2).setCellValue("任务完成度");

        List<TrackIndicatorInfo> indicatorInfoList = taskIndicatorRes.getIndicatorInfoList();
        for (int i = 0; i < indicatorInfoList.size(); i++) {
            TrackIndicatorInfo info = indicatorInfoList.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(info.getScene());
            row.createCell(1).setCellValue(info.getTaskSuccessRate());
            row.createCell(2).setCellValue(info.getTaskCompletion());
        }
        Row summaryRow = sheet.createRow(indicatorInfoList.size() + 1);
        summaryRow.createCell(0).setCellValue("整体任务");
        summaryRow.createCell(1).setCellValue(taskIndicatorRes.getTotalTaskSuccessRate());
        summaryRow.createCell(2).setCellValue(taskIndicatorRes.getTotalTaskCompletion());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=task_indicators.xlsx")
                .header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(new ByteArrayInputStream(outputStream.toByteArray()));
    }


    /**
     * queryReq queryReq
     * @param queryReq queryReq
     * @return
     */
    public CommonResponse modelVersionList(ModelQueryReq queryReq) {
        return new CommonResponse();
    }

    private void exportTrackDetails(List<TrackExportDetail> trackExportDetails, AgentEvalJobInstanceDo agentEvalJobInstanceDo, TenantNasInfo tenantNasInfo) {
        String system = System.getProperty("os.name").toLowerCase();
        String excelFilePath;
        if (system.contains("win")) {
            // window test files
            excelFilePath = FilePathUtils.buildFilePath("D:/work/agent-simulation/agent_eval/ui_agent/",
                    agentEvalJobInstanceDo.getJobId(), agentEvalJobInstanceDo.getId(), Constants.AGENT_RESULT);
        } else {
            // linux test files
            excelFilePath = FilePathUtils.buildFilePath(Constants.IMG_ROOT_PATH, tenantNasInfo.getNasDir(), OperatorUtils.getNamespace(),
                    agentEvalJobInstanceDo.getJobId(), agentEvalJobInstanceDo.getId(), Constants.AGENT_RESULT);
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("agent-result");
            // Create header
            String[] headers = {"编号", "设备型号", "模型名", "结果", "应用", "APP版本", "指令", "DAG", "目标步数", "实际步数", "轨迹详情"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            // Create data rows
            int rowNum = 1;
            for (TrackExportDetail detail : trackExportDetails) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(detail.getId());
                row.createCell(1).setCellValue(detail.getDeviceName());
                row.createCell(2).setCellValue(detail.getModelName());
                row.createCell(3).setCellValue(detail.getScene());
                row.createCell(4).setCellValue(detail.getAppName());
                row.createCell(5).setCellValue(detail.getAppVersion());
                row.createCell(6).setCellValue(detail.getInstruction());
                row.createCell(7).setCellValue(detail.getDag());
                row.createCell(8).setCellValue(detail.getTargetStepNum());
                row.createCell(9).setCellValue(detail.getRealStepNum());
                row.createCell(10).setCellValue(detail.getTrackDetailInfo());
            }

            // Automatically adjust column width
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            // write file
            try (FileOutputStream fileOut = new FileOutputStream(excelFilePath)) {
                workbook.write(fileOut);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<TrackExportDetail> buildTrackExportDetails(List<String> trackIdList, AgentEvalJobInstanceDo agentEvalJobInstanceDo) {
        AgentEvalJobDo agentEvalJobDo = agentEvalJobMapper.selectById(agentEvalJobInstanceDo.getJobId());
        if (Objects.isNull(agentEvalJobDo)) {
            throw new BusinessException(BizErrorCode.SERVER_ERROR, "job not found.");
        }
        LambdaQueryWrapper<AgentDeviceReferenceDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentDeviceReferenceDo::getTaskId, agentEvalJobDo.getTaskId())
                .orderByDesc(AgentDeviceReferenceDo::getCreateTime);
        List<AgentDeviceReferenceDo> agentDeviceReferenceDos = agentDeviceRefMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(agentDeviceReferenceDos)) {
            throw new BusinessException(BizErrorCode.SERVER_ERROR, "devices not found.");
        }
        List<TrackExportDetail> trackExportDetails = new ArrayList<>();
        if(CollectionUtils.isEmpty(trackIdList)) {
            LambdaQueryWrapper<AgentEvalJobInstanceTrackDo> trackWrapper = new LambdaQueryWrapper<>();
            trackWrapper.eq(AgentEvalJobInstanceTrackDo::getJobInstanceId, agentEvalJobInstanceDo.getId());
            List<AgentEvalJobInstanceTrackDo> agentEvalJobInstanceTrackDoList = agentEvalJobInstanceTrackMapper.selectList(trackWrapper);
            trackIdList = agentEvalJobInstanceTrackDoList.stream()
                    .map(AgentEvalJobInstanceTrackDo::getId)
                    .collect(Collectors.toList());
        }
        for (String trackId : trackIdList) {
            AgentEvalJobInstanceTrackDo agentEvalJobInstanceTrackDo = agentEvalJobInstanceTrackMapper.selectById(trackId);
            String trackInfo = agentEvalJobInstanceTrackDo.getTrackInfo();
            InstanceTrackInfo instanceTrackInfo = JsonUtils.parseObject(trackInfo, InstanceTrackInfo.class);
            String trackDetail = agentEvalJobInstanceTrackDo.getTrackDetail();
            TrackExportDetail trackExportDetail = new TrackExportDetail();
            BeanUtils.copyProperties(instanceTrackInfo, trackExportDetail);
            trackExportDetail.setTrackDetailInfo(trackDetail);
            trackExportDetail.setId(trackId);
            trackExportDetail.setModelName(agentEvalJobDo.getModelName());
            trackExportDetail.setDeviceName(agentDeviceReferenceDos.get(0).getDeviceName());
            trackExportDetails.add(trackExportDetail);
        }
        return trackExportDetails;
    }


    private Integer calculateTargetStep(String trackInfo) {
        InstanceTrackInfo instanceTrackInfo = JsonUtils.parseObject(trackInfo, InstanceTrackInfo.class);
        String[] steps = instanceTrackInfo.getDag();
        return steps.length;
    }

    private void calculateTaskProgress(List<InstanceTrackDetailInfo> trackDetailInfoList, InstanceTrackInfo instanceTrackInfo) {
        int successCount = 0;
        for (InstanceTrackDetailInfo trackDetailInfo : trackDetailInfoList) {
            if (trackDetailInfo.getIsSuccess()) {
                successCount++;
            }
        }
        double taskProgressPercentage = ((double) successCount / calculateTargetStep(JsonUtils.toJsonString(instanceTrackInfo))) * 100;
        taskProgressPercentage = Math.min(taskProgressPercentage, 100.0);
        String taskProgress = String.format("%.0f%%", taskProgressPercentage);
        instanceTrackInfo.setTaskProgress(taskProgress);
    }
}
