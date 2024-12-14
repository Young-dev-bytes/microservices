package com.in28minutes.microservices.mlagenteval.controller;

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
@RestController
@RequestMapping(path = "/aicloud-ml/agent/v1/job", produces = "application/json")
public class AgentEvalJobController {
    @Autowired
    private AgentEvalTaskService evalTaskService;

    @Autowired
    private AgentEvalJobService evalJobService;

    /**
     * save job
     *
     * @param request CloudDeviceJobPageReq
     * @return 返回响应
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
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
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public CommonResponse getJobListByTask(@Valid @RequestBody AgentEvalJobPageReq request) {
        return CommonResponse.successResponse(evalJobService.getJobListByTask(request));
    }

    /**
     * job detail
     *
     * @param jobId jobId
     * @return 返回响应
     */
    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    public CommonResponse getJobDetail(@RequestParam String jobId) {
        return CommonResponse.successResponse(evalJobService.getJobDetail(jobId));
    }

    /**
     * deleteBatch
     *
     * @param jobIdLists jobIdLists
     * @return CommonResponse
     */
    @RequestMapping(value = "/deleteBatch", method = RequestMethod.POST)
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
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResponse updateJob(@RequestParam String jobId) {
        return CommonResponse.successResponse(evalJobService.getJobDetail(jobId));
    }

    /**
     * execJob
     *
     * @param request request
     * @return CommonResponse
     */
    @RequestMapping(value = "/exec", method = RequestMethod.POST)
    public CommonResponse startExecJob(@Valid @RequestBody AgentEvalJobExecReq request) {
        evalJobService.startExecJob(request);
        return CommonResponse.successResponse();
    }

    /**
     * jobTrackPage
     *
     * @param agentEvalInstTrackReq agentEvalJobInstReq
     * @return CommonResponse
     */
    @RequestMapping(value = "/track/page", method = RequestMethod.POST)
    public CommonResponse jobTrackPage(@Valid @RequestBody AgentEvalInstTrackReq agentEvalInstTrackReq) {
        return CommonResponse.successResponse(evalJobService.jobTrackPage(agentEvalInstTrackReq));
    }

    /**
     * jobTrackDetail
     *
     * @param agentEvalInstTrackReq agentEvalInstTrackReq
     * @return CommonResponse
     */
    @RequestMapping(value = "/track/detail", method = RequestMethod.POST)
    public CommonResponse jobTrackDetail(@Valid @RequestBody AgentEvalInstTrackReq agentEvalInstTrackReq) {
        return CommonResponse.successResponse(evalJobService.jobTrackDetail(agentEvalInstTrackReq));
    }

    /**
     * jobTrackUpdate
     *
     * @param agentEvalInstTrackReq agentEvalInstTrackReq
     * @return CommonResponse
     */
    @RequestMapping(value = "/track/update", method = RequestMethod.POST)
    public CommonResponse jobTrackUpdate(@Valid @RequestBody AgentEvalInstTrackReq agentEvalInstTrackReq) {
        evalJobService.jobTrackUpdate(agentEvalInstTrackReq);
        return CommonResponse.successResponse();
    }

    /**
     * checkStepImages
     *
     * @param imgPath imgPath
     * @return CommonResponse
     */
    @RequestMapping(value = "/track/update", method = RequestMethod.POST)
    public CommonResponse checkStepImages(@RequestParam String imgPath) {
        evalJobService.checkStepImages(imgPath);
        return CommonResponse.successResponse();
    }
}
