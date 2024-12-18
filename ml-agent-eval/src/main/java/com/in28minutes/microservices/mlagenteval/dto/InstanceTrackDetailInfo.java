package com.in28minutes.microservices.mlagenteval.dto;


import lombok.Data;

@Data
public class InstanceTrackDetailInfo {
    private String observation;

    private String thought;

    private String action;

    private String step;

    private String imagePathBefore;

    private String imagePathAfter;

    private Boolean isSuccess;

    private String failMsg;
}
