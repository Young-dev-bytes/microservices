package com.young.microservices.mlagenteval.dto;


import lombok.Data;

@Data
public class InstanceTrackInfo {
    private String trackId;

    private String scene;

    private String appName;

    private String appVersion;

    private String[] dag;

    private String instruction;

    private Integer targetStepNum;

    private Integer realStepNum;

    private String assistEvalResult;

    private Boolean isSuccess;

    private String taskProgress;
}
