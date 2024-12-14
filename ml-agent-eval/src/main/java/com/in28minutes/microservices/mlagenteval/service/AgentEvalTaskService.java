package com.in28minutes.microservices.mlagenteval.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.in28minutes.microservices.mlagenteval.dao.entity.AgentDeviceReferenceDo;
import com.in28minutes.microservices.mlagenteval.dao.entity.AgentEvalTaskDo;
import com.in28minutes.microservices.mlagenteval.dao.mapper.AgentDeviceReferenceMapper;
import com.in28minutes.microservices.mlagenteval.dao.mapper.AgentEvalTaskMapper;
import com.in28minutes.microservices.mlagenteval.dto.*;
import com.in28minutes.microservices.mlagenteval.enums.BizErrorCode;
import com.in28minutes.microservices.mlagenteval.enums.DeviceOccupyStatusEnum;
import com.in28minutes.microservices.mlagenteval.exception.BusinessException;
import com.in28minutes.microservices.mlagenteval.utils.UuidUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 功能描述:agent在线评估任务管理表
 *
 * @author cw0106718
 * @since 2024-11-25
 */

@Service
public class AgentEvalTaskService extends ServiceImpl<AgentEvalTaskMapper, AgentEvalTaskDo> {

    @Autowired
    private AgentEvalTaskMapper agentEvalTaskMapper;

    @Autowired
    private AgentDeviceReferenceMapper agentDeviceReferenceMapper;


    /**
     * save online agent task
     *
     * @param request request
     * @return String
     */
    @Transactional(rollbackFor = Exception.class)
    public void save(AgentEvalTaskSaveReq request) {
        AgentEvalTaskDo taskDo = new AgentEvalTaskDo();
        BeanUtils.copyProperties(request, taskDo);
        taskDo.setId(UuidUtils.genSimpleUuid());
        taskDo.setEvalTaskType("UIAgent");
        save(taskDo);
        List<DeviceInfo> deviceInfoList = request.getDeviceInfoList();
        /*for (DeviceInfo deviceInfo : deviceInfoList) {
            // Request tenant interface to obtain device information and attempt to occupy
            CommonResponse response =
                    tenantProxy.occupyDevice(deviceInfo.getDeviceUdid(), OperatorUtils.getOperator(), taskDo.getId());
            if (!response.getCode().equals(CommonResponse.SUCCESS_STATUS)) {
                //todo:// if occupy failed, then release all devices.
                throw new BusinessException(AgentEvalErrorCode.OCCUPY_DEVICE_FAILED_ERROR);
            }
        }*/

        for (DeviceInfo deviceInfo : deviceInfoList) {
            AgentDeviceReferenceDo agentDeviceReferenceDo = new AgentDeviceReferenceDo();
            BeanUtils.copyProperties(deviceInfo, agentDeviceReferenceDo);
            agentDeviceReferenceDo.setTaskId(taskDo.getId());
            agentDeviceReferenceDo.setDeviceOccupyStatus(DeviceOccupyStatusEnum.OCCUPIED.getValue());
            /*agentDeviceReferenceDo.setOccupyUser(OperatorUtils.getOperator());*/
            agentDeviceReferenceDo.setOccupyTime(LocalDateTime.now());
            agentDeviceReferenceDo.setTimeout(request.getTimeout());
            agentDeviceReferenceMapper.insert(agentDeviceReferenceDo);
        }
    }

    /**
     * page query
     *
     * @param request PageRequest
     * @return PageBaseResponse
     */
    public PageBaseResponse<AgentEvalTaskDetail> pageByProject(AgentEvalTaskPageReq request) {
        /*request.setProjectId(OperatorUtils.getProjectId());
        request.setTenantId(OperatorUtils.getTenantId());*/
        /*LambdaQueryWrapper<AgentEvalTaskDo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AgentEvalTaskDo::getProjectId, request.getProjectId())
                .eq(AgentEvalTaskDo::getTenantId, request.getTenantId())
                .orderByDesc(AgentEvalTaskDo::getUpdateTime);
        if (StringUtils.isNotEmpty(request.getEvalTaskName())) {
            lambdaQueryWrapper.like(AgentEvalTaskDo::getEvalTaskName, request.getEvalTaskName());
        }
        if (StringUtils.isNotEmpty(request.getCreateUser())) {
            lambdaQueryWrapper.eq(AgentEvalTaskDo::getCreateUser, request.getCreateUser());
        }

        Page<AgentEvalTaskDo> page = new Page<>(request.getCurPage(), request.getPageSize());
        page = agentEvalTaskMapper.selectPage(page, lambdaQueryWrapper);
        List<AgentEvalTaskDo> taskDoList = page.getRecords();
        if (CollectionUtils.isEmpty(taskDoList)) {
            return null;
        }

        PageBaseResponse<AgentEvalTaskDetail> detailPageList =
                new PageBaseResponse<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<AgentEvalTaskDetail> taskDetailList = new ArrayList<>();
        for (AgentEvalTaskDo taskDo : taskDoList) {
            taskDetailList.add(getTaskDetail(taskDo));
        }
        detailPageList.setRecords(taskDetailList);
        return detailPageList;*/
        return null;
    }

    /**
     * Batch delete test tasks
     *
     * @param request request
     */
    public void deleteTaskList(AgentEvalTaskDeleteReq request) {
        for (String taskId : request.getTaskDeleteList()) {
            deleteTaskDo(taskId);
        }
    }

    /**
     * occupy devices
     *
     * @param req req
     * @return CommonResponse
     */
    public void occupyDevice(AgentRelOccReq req) {
        AgentEvalTaskDo taskDo = lambdaQuery().eq(AgentEvalTaskDo::getId, req.getTaskId()).one();
        if(Objects.isNull(taskDo)) {
            throw new BusinessException(BizErrorCode.SERVER_ERROR, "Task does not exist.");
        }
        List<DeviceInfo> deviceInfoList = req.getDeviceInfoList();
        if(CollectionUtils.isEmpty(deviceInfoList)) {
            throw new BusinessException(BizErrorCode.SERVER_ERROR, "pls select device.");
        }
        /*for (DeviceInfo deviceInfo : deviceInfoList) {
            CommonResponse response =
                    tenantProxy.occupyDevice(deviceInfo.getDeviceUdid(), OperatorUtils.getOperator(), taskDo.getId());
            if (!response.getCode().equals(CommonResponse.SUCCESS_STATUS)) {
                //todo:// if occupy failed, then release all devices.
                throw new BusinessException(BizErrorCode.SERVER_ERROR, "设备占用失败，请使用'占用设备'按钮重新占用.");
            }
        }*/
        for (DeviceInfo deviceInfo : deviceInfoList) {
            LambdaQueryWrapper<AgentDeviceReferenceDo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(AgentDeviceReferenceDo::getDeviceUdid, deviceInfo.getDeviceUdid());
            AgentDeviceReferenceDo agentDeviceReferenceDo = agentDeviceReferenceMapper.selectOne(lambdaQueryWrapper);

            if(!Objects.isNull(agentDeviceReferenceDo)) {
                agentDeviceReferenceDo.setTimeout(req.getTimeout())
                        .setOccupyUser("cw0106718")
                        .setOccupyTime(LocalDateTime.now())
                        .setTaskId(req.getTaskId())
                        .setDeviceOccupyStatus(DeviceOccupyStatusEnum.OCCUPIED.getValue());
                agentDeviceReferenceMapper.updateById(agentDeviceReferenceDo);
            } else {
                AgentDeviceReferenceDo agentDeviceRef = new AgentDeviceReferenceDo();
                agentDeviceRef.setTimeout(req.getTimeout());
                agentDeviceRef.setId(UuidUtils.genSimpleUuid());
                agentDeviceRef.setOccupyUser("cw0106718");
                agentDeviceRef.setOccupyTime(LocalDateTime.now());
                agentDeviceRef.setTaskId(req.getTaskId());
                agentDeviceRef.setDeviceOccupyStatus(DeviceOccupyStatusEnum.OCCUPIED.getValue());
                agentDeviceRef.setDeviceName(deviceInfo.getDeviceName());
                agentDeviceRef.setDeviceUdid(deviceInfo.getDeviceUdid());
                agentDeviceReferenceMapper.insert(agentDeviceRef);
            }
        }
    }

    /**
     * release Device
     *
     * @param request request
     */
    public void freeDevice(AgentRelOccReq request) {
        // Determine whether the current status of the task is occupied
        /*LambdaQueryWrapper<AgentDeviceRefDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentDeviceRefDo::getTaskId, request.getTaskId());
        List<AgentDeviceRefDo> agentDeviceRefDoList = agentDeviceRefMapper.selectList(queryWrapper);
        boolean anyMatch = agentDeviceRefDoList.stream().anyMatch(agentDeviceRefDo ->
                DeviceOccupyStatusEnum.OCCUPIED.getValue().equals(agentDeviceRefDo.getDeviceOccupyStatus()));
        if (anyMatch) {
            throw new BusinessException(BizErrorCode.OPERATION_NOT_SUPPORT, "The current task has device occupy.");
        }*/
        // Determine if there are any commands being executed under the task
        // todo:// 选择释放设备时，需判断当前设备是否有任务正在执行，存在则提示：xx设备当前正在执行任务，无法释放
        /*List<CloudDeviceJobDo> jobList = jobService.lambdaQuery()
                .eq(CloudDeviceJobDo::getTaskId, taskId)
                .orderByDesc(CloudDeviceJobDo::getCreateTime)
                .list();
        if (!CollectionUtils.isEmpty(jobList)) {
            if (!CloudDeviceJobStatusEnum.getEndStatus().contains(jobList.get(0).getStatus())) {
                throw new BusinessException(BizErrorCode.OPERATION_NOT_SUPPORT);
            }
        }*/

        List<DeviceInfo> deviceInfoList = request.getDeviceInfoList();
        for (DeviceInfo deviceInfo : deviceInfoList) {
            // 删除设备上文件
            // tenantProxy.freeDevice(deviceInfo.getDeviceUdid());
            updateAfterFree(deviceInfo.getDeviceUdid(), request.getTaskId());
        }
    }

    private AgentEvalTaskDetail getTaskDetail(AgentEvalTaskDo taskDo) {
        AgentEvalTaskDetail taskDetail = new AgentEvalTaskDetail();
        List<DeviceInfo> deviceInfoList = new ArrayList<>();
        BeanUtils.copyProperties(taskDo, taskDetail);
        List<AgentDeviceReferenceDo> agentDeviceReferenceDoList = getAgentDeviceRefDos(taskDo);
        agentDeviceReferenceDoList.stream()
                .map(agentDeviceReferenceDo -> {
                    DeviceInfo deviceInfo = new DeviceInfo();
                    BeanUtils.copyProperties(agentDeviceReferenceDo, deviceInfo);
                    return deviceInfo;
                })
                .forEach(deviceInfoList::add);
        taskDetail.setDeviceInfoList(deviceInfoList);
        return taskDetail;
    }

    private List<AgentDeviceReferenceDo> getAgentDeviceRefDos(AgentEvalTaskDo taskDo) {
        LambdaQueryWrapper<AgentDeviceReferenceDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentDeviceReferenceDo::getTaskId, taskDo.getId());
        return agentDeviceReferenceMapper.selectList(queryWrapper);
    }

    /**
     * Delete a testing task
     *
     * @param taskId String
     */
    public void deleteTaskDo(String taskId) {
        if (StringUtils.isBlank(taskId)) {
            throw new BusinessException(BizErrorCode.PARAM_INVALID);
        }
        AgentEvalTaskDo taskDo = getById(taskId);
        if (Objects.isNull(taskDo)) {
            throw new BusinessException(BizErrorCode.PARAM_INVALID, "Task does not exist。");
        }
        AgentEvalTaskDo queryTaskDo = new AgentEvalTaskDo();
        queryTaskDo.setId(taskId);
        List<AgentDeviceReferenceDo> agentDeviceReferenceDos = getAgentDeviceRefDos(queryTaskDo);
        boolean anyMatch = agentDeviceReferenceDos.stream().anyMatch(agentDeviceReferenceDo ->
                DeviceOccupyStatusEnum.OCCUPIED.getValue().equals(agentDeviceReferenceDo.getDeviceOccupyStatus()));
        if (anyMatch) {
            throw new BusinessException(BizErrorCode.OPERATION_NOT_SUPPORT, "The current task has device occupy.");
        }
        // todo: check testing task
        /*List<CloudDeviceJobDo> jobs = jobService.lambdaQuery().eq(CloudDeviceJobDo::getTaskId, taskId).list();
        if (!CollectionUtils.isEmpty(jobs)) {
            throw new BusinessException(BizErrorCode.OPERATION_NOT_SUPPORT, "该任务下存在测试任务，请先删除测试任务。");
        }*/
        agentEvalTaskMapper.deleteById(taskId);
        LambdaQueryWrapper<AgentDeviceReferenceDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentDeviceReferenceDo::getTaskId, taskId);
        agentDeviceReferenceMapper.delete(queryWrapper);
    }

    /**
     * Modify database table information after releasing timeout occupied devices
     *
     * @param udid udid
     */
    public void updateAfterFree(String udid, String taskId) {
        LambdaQueryWrapper<AgentDeviceReferenceDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentDeviceReferenceDo::getDeviceUdid, udid)
                .eq(AgentDeviceReferenceDo::getTaskId, taskId);
        AgentDeviceReferenceDo agentDeviceReferenceDo = agentDeviceReferenceMapper.selectOne(queryWrapper);
        if(!Objects.isNull(agentDeviceReferenceDo)){
            agentDeviceReferenceMapper.delete(queryWrapper);
        }
    }
}
