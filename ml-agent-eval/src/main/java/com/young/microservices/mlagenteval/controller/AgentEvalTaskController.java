package com.young.microservices.mlagenteval.controller;

import com.young.microservices.mlagenteval.common.CommonResponse;
import com.in28minutes.microservices.mlagenteval.dto.*;
import com.young.microservices.mlagenteval.enums.OccupyTimeOutEnum;
import com.young.microservices.mlagenteval.service.AgentEvalJobService;
import com.young.microservices.mlagenteval.service.AgentEvalTaskService;
import com.young.microservices.mlagenteval.dto.*;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 功能描述:
 *
 * @author cw0106718
 * @since 2024-12-3
 */
@RestController
@RequestMapping(path = "/aicloud-ml/agent/v1/task", produces = "application/json")
public class AgentEvalTaskController {
    @Autowired
    private AgentEvalTaskService evalTaskService;

    @Autowired
    private AgentEvalJobService evalJobService;

    /**
     * submit save
     *
     * @param request request
     * @return CommonResponse
     */
    @RequestMapping(path = "/save", method = RequestMethod.POST)
    public CommonResponse save(@Valid @RequestBody AgentEvalTaskSaveReq request) {
        evalTaskService.save(request);
        return CommonResponse.successResponse();
    }

    /**
     * page
     *
     * @param request request
     * @return PageBaseResponse
     */
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public PageBaseResponse<AgentEvalTaskDetail> queryTaskByPage(@Valid @RequestBody AgentEvalTaskPageReq request) {
        return evalTaskService.pageByProject(request);
    }

    /**
     * batchDelete
     *
     * @param request request
     * @return CommonResponse
     */
    @RequestMapping(value = "/deleteList", method = RequestMethod.POST)
    public CommonResponse batchDelete(@Valid @RequestBody AgentEvalTaskDeleteReq request) {
        evalTaskService.deleteTaskList(request);
        return CommonResponse.successResponse();
    }

    /**
     * get task detail
     *
     * @param taskId taskId
     * @return CommonResponse
     */
    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    public CommonResponse queryTaskDetail(@RequestParam("taskId")String taskId) {
        return CommonResponse.successResponse(evalTaskService.queryTaskDetail(taskId));
    }

    /**
     * Occupation timeout enumeration
     *
     * @return CommonResponse
     */
    @RequestMapping(value = "/timeout/enum", method = RequestMethod.GET)
    public CommonResponse getTimeOutEnum() {
        List<Pair<String, String>> statusList = Arrays.stream(OccupyTimeOutEnum.values())
                .map(item -> Pair.of(item.getValue(), item.getDesc()))
                .collect(Collectors.toList());
        return CommonResponse.successResponse(statusList);
    }

    /**
     * Occupy devices
     *
     * @param req req
     * @return CommonResponse
     */
    @RequestMapping(value = "/device/occupy", method = RequestMethod.POST)
    public CommonResponse occupyDevice(@Valid @RequestBody AgentRelOccReq req) {
        evalTaskService.occupyDevice(req);
        return CommonResponse.successResponse();
    }

    /**
     * release devices
     *
     * @param req req
     * @return CommonResponse
     */
    @RequestMapping(value = "/device/free", method = RequestMethod.POST)
    public CommonResponse freeDevice(@Valid @RequestBody AgentRelOccReq req) {
        evalTaskService.freeDevice(req);
        return CommonResponse.successResponse();
    }
}
