package com.young.microservices.mlagenteval.dto;

import lombok.Data;

@Data
public class TrackIndicatorInfo {
    private String scene;

    private String taskSuccessRate;

    private String taskCompletion;
}
