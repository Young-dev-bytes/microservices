package com.young.microservices.mlagenteval.dto;

import lombok.Data;

@Data
public class TrackExportDetail {
    private String id;

    private String deviceName;

    private String modelName;

    private String scene;

    private String appName;

    private String appVersion;

    private String instruction;

    private String dag;

    private String targetStepNum;

    private String realStepNum;

    private String trackDetailInfo;
}
