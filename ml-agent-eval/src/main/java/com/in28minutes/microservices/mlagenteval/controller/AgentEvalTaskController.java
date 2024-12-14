package com.in28minutes.microservices.mlagenteval.controller;

import com.in28minutes.microservices.mlagenteval.common.CommonResponse;
import com.in28minutes.microservices.mlagenteval.dto.*;
import com.in28minutes.microservices.mlagenteval.service.AgentEvalJobService;
import com.in28minutes.microservices.mlagenteval.service.AgentEvalTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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
    public Object queryTaskByPage(@Valid @RequestBody AgentEvalTaskPageReq request) {
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
