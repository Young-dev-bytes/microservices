package com.in28minutes.microservices.mlagenteval.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentEvalJobReq {

    private String id;

    private String taskId;

    private String jobName;

    @NotBlank
    private String nasAddress;

    @NotBlank
    private String datasetPath;

    private String params;

    @NotBlank
    private String promptTemplate;

    @NotNull
    private Integer isHistoryActions;

    @NotBlank
    private String modelName;

    @NotBlank
    private String inferUrl;

    @NotNull
    private Integer executeTurn;

    private Integer isStepEval;

    private Integer isJobEval;
}
