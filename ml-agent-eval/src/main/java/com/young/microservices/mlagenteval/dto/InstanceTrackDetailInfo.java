package com.young.microservices.mlagenteval.dto;


import lombok.Data;

@Data
public class InstanceTrackDetailInfo {
    private String observation;

    private String thought;

    private String action;

    private String step;

    private String imagePathBefore;

    private String imagePathAfter;

    private String assistEval;

    private String assistEvalAnalysis;

    private Boolean isSuccess;

    private String failMsg;
}
