package com.young.microservices.mlagenteval.controller;

import com.young.microservices.mlagenteval.common.CommonResponse;
import com.young.microservices.mlagenteval.constant.Constants;
import com.in28minutes.microservices.mlagenteval.dto.*;
import com.young.microservices.mlagenteval.dto.*;
import com.young.microservices.mlagenteval.service.AgentEvalJobService;
import com.young.microservices.mlagenteval.service.AgentEvalTaskService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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


    /************JOB START************************/
    /**
     * save job
     *
     * @param request CloudDeviceJobPageReq
     * @return 返回响应
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResponse saveJob(@Valid @RequestBody AgentEvalJobReq request) {
        return CommonResponse.successResponse(evalJobService.saveJob(request));
    }

    /**
     * job update
     *
     * @param request request
     * @return 返回响应
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResponse updateJob(@Valid @RequestBody AgentEvalJobReq request) {
        return CommonResponse.successResponse(evalJobService.updateJob(request));
    }

    /**
     * page
     *
     * @param request CloudDeviceJobPageReq
     * @return 返回响应
     */
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public CommonResponse retrieveJobPage(@Valid @RequestBody AgentEvalJobPageReq request) {
        return CommonResponse.successResponse(evalJobService.retrieveJobPage(request));
    }

    /**
     * job detail
     *
     * @param jobId jobId
     * @return 返回响应
     */
    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    public CommonResponse retrieveJobDetail(@RequestParam String jobId) {
        return CommonResponse.successResponse(evalJobService.retrieveJobDetail(jobId));
    }

    /**
     * jobDelete
     *
     * @param jobId jobId
     * @return CommonResponse
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public CommonResponse jobDelete(@RequestParam String jobId) {
        evalJobService.jobDelete(jobId);
        return CommonResponse.successResponse();
    }

    /************JOB END************************/



    /************JOB INSTANCE START****************/

    /**
     * exec instance Job
     *
     * @param request request
     * @return CommonResponse
     */
    @RequestMapping(value = "/instance/exec", method = RequestMethod.POST)
    public CommonResponse startExecInstanceJob(@Valid @RequestBody AgentEvalJobExecReq request) {
        evalJobService.startExecInstanceJob(request);
        return CommonResponse.successResponse();
    }

    /**
     * stop instance job
     *
     * @param jobId jobId
     * @return CommonResponse
     */
    @RequestMapping(value = "/instance/stop", method = RequestMethod.POST)
    public CommonResponse stopInstanceJob(@RequestParam String jobId) {
        evalJobService.stopInstanceJob(jobId);
        return CommonResponse.successResponse();
    }

    /**
     * retrieve Instance Page
     *
     * @param request request
     * @return CommonResponse
     */
    @RequestMapping(value = "/instance/page", method = RequestMethod.POST)
    public PageBaseResponse<AgentEvalJobInstanceRes> retrieveInstancePage(@Valid @RequestBody AgentEvalJobInstancePageReq request) {
        return evalJobService.retrieveInstancePage(request);
    }
    /************JOB INSTANCE END****************/



    /************JOB INSTANCE TRACK START****************/
    /**
     * jobTrackPage
     *
     * @param agentEvalInstTrackReq agentEvalJobInstReq
     * @return CommonResponse
     */
    @RequestMapping(value = "/track/page", method = RequestMethod.POST)
    public PageBaseResponse<InstanceTrackInfo> jobTrackPage(@Valid @RequestBody AgentEvalInstTrackReq agentEvalInstTrackReq) {
        return evalJobService.jobTrackPage(agentEvalInstTrackReq);
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
    @RequestMapping(value = "/track/checkImg", method = RequestMethod.GET)
    public CommonResponse checkStepImages(@RequestParam String imgPath) {
        return CommonResponse.successResponse(evalJobService.checkStepImages(imgPath));
    }

    /**
     * trackBatchDelete
     *
     * @param request request
     * @return CommonResponse
     */
    @RequestMapping(value = "/track/batchDelete", method = RequestMethod.POST)
    public CommonResponse trackBatchDelete(@Valid @RequestBody AgentEvalJobTrackDelReq request) {
        evalJobService.trackBatchDelete(request);
        return CommonResponse.successResponse();
    }

    /**
     * trackBatchDelete
     *
     * @param request request
     * @return CommonResponse
     */
    @RequestMapping(value = "/track/download", method = RequestMethod.GET)
    @ApiResponses({@ApiResponse(code = Constants.CODE_OK, response = File.class, message = "")})
    public ResponseEntity<InputStream> trackDownload(@Valid @RequestBody AgentEvalJobTrackDownReq request) throws IOException {
        return evalJobService.trackDownload(request);
    }
    /************JOB INSTANCE TRACK END****************/
}
