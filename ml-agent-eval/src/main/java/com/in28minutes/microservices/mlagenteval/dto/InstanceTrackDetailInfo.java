package com.in28minutes.microservices.mlagenteval.dto;


import lombok.Data;

@Data
public class InstanceTrackDetailInfo {
    private String observation;

    private String thought;

    private String action;

    private String step;

    private String imagePath;

    private Boolean isSuccess;

    private String failMsg;
}
