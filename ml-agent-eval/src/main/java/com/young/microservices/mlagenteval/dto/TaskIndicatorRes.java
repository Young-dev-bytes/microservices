package com.young.microservices.mlagenteval.dto;

import lombok.Data;

import java.util.List;

@Data
public class TaskIndicatorRes {
    private String totalTaskSuccessRate;

    private String totalTaskCompletion;

    List<TrackIndicatorInfo> indicatorInfoList;
}
