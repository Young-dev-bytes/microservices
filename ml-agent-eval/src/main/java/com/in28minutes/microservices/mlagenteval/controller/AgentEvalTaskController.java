package com.in28minutes.microservices.mlagenteval.controller;


//package com.hihonor.ml.agent.controller;
//
//import com.hihonor.ml.agent.dao.entity.AgentEvalJobDo;
//import com.hihonor.ml.agent.dto.*;
//import com.hihonor.ml.agent.enums.OccupyTimeOutEnum;
//import com.hihonor.ml.agent.service.AgentEvalJobService;
//import com.hihonor.ml.agent.service.AgentEvalTaskService;
//import com.hihonor.ml.common.biz.dto.base.CommonResponse;
//import com.hihonor.ml.common.biz.dto.base.PageBaseResponse;
//import com.hihonor.zeus.common.tool.exception.BusinessException;
//import org.apache.commons.lang3.tuple.Pair;
//import org.apache.servicecomb.provider.rest.common.RestSchema;

import com.in28minutes.microservices.mlagenteval.common.CommonResponse;
import com.in28minutes.microservices.mlagenteval.dto.*;
import com.in28minutes.microservices.mlagenteval.service.AgentEvalJobService;
import com.in28minutes.microservices.mlagenteval.service.AgentEvalTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 功能描述:
 *
 * @author cw0106718
 * @since 2024-12-3
 */
//@RestSchema(schemaId = "model")
@RestController
@RequestMapping(path = "/aicloud-ml/agent/v1", produces = "application/json")
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
    @RequestMapping(path = "/task/save", method = RequestMethod.POST)
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
    @RequestMapping(value = "/task/page", method = RequestMethod.POST)
    public Object queryTaskByPage(@Valid @RequestBody AgentEvalTaskPageReq request) {
        return evalTaskService.pageByProject(request);
    }

    /**
     * batchDelete
     *
     * @param request request
     * @return CommonResponse
     */
    @RequestMapping(value = "/task/deleteList", method = RequestMethod.POST)
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

    /**
     * save job
     *
     * @param request CloudDeviceJobPageReq
     * @return 返回响应
     */
    @RequestMapping(value = "/job/save", method = RequestMethod.POST)
    public CommonResponse getJobList(@Valid @RequestBody AgentEvalJobSaveReq request) {
        evalJobService.save(request);
        return CommonResponse.successResponse();
    }

    /**
     * page
     *
     * @param request CloudDeviceJobPageReq
     * @return 返回响应
     */
    @RequestMapping(value = "/job/page", method = RequestMethod.POST)
    public CommonResponse getJobListByTask(@Valid @RequestBody AgentEvalJobPageReq request) {
        return CommonResponse.successResponse(evalJobService.getJobListByTask(request));
    }

    /**
     * job detail
     *
     * @param jobId jobId
     * @return 返回响应
     */
    @RequestMapping(value = "/job/detail", method = RequestMethod.POST)
    public CommonResponse getJobDetail(@RequestParam String jobId) {
        return CommonResponse.successResponse(evalJobService.getJobDetail(jobId));
    }


    /**
     * deleteBatch
     *
     * @param jobIdLists jobIdLists
     * @return CommonResponse
     */
    @RequestMapping(value = "/job/deleteBatch", method = RequestMethod.POST)
    @ResponseBody
    public CommonResponse jobDeleteBatch(@RequestBody List<String> jobIdLists) {
        // evalJobService.deleteJobBatch(jobIdLists);
        return CommonResponse.successResponse();
    }


    /**
     * job update
     *
     * @param jobId jobId
     * @return 返回响应
     */
    @RequestMapping(value = "/job/update", method = RequestMethod.POST)
    public CommonResponse updateJob(@RequestParam String jobId) {
        return CommonResponse.successResponse(evalJobService.getJobDetail(jobId));
    }


    /**
     * execJob
     *
     * @param request request
     * @return CommonResponse
     */
    @RequestMapping(value = "/job/exec", method = RequestMethod.POST)
    public CommonResponse startExecJob(@Valid @RequestBody AgentEvalJobExecReq request) {
        evalJobService.startExecJob(request);
        return CommonResponse.successResponse();
    }

}
