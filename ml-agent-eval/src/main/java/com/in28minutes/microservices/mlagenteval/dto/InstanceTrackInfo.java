package com.in28minutes.microservices.mlagenteval.dto;


import lombok.Data;

@Data
public class InstanceTrackInfo {
    private String trackId;
    private String scene;
    private String appName;
    private String appVersion;
    private String dag;
    private String instruction;
    private String targetStepNum;
    private String realStepNum;
    private Boolean isSuccess;
    private String taskProgress;
}
