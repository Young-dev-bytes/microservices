package com.in28minutes.microservices.mlagenteval.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AgentEvalJobSaveReq {
    private String taskId;

    private String jobName;

    @NotBlank
    private String datasetId;

    private String params;

    @NotBlank
    private String promptTemplate;

    private Integer isHistoryActions;

    @NotBlank
    private String modelName;

    @NotBlank
    private String inferUrl;

    private Integer executeTurn;
    private Integer isStepEval;

    private Integer isJobEval;

    private String projectId;

    private String tenantId;

    private String createUser;

    private String updateUser;
}
