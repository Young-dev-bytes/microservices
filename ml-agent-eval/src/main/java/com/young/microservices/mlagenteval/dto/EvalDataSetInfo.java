package com.young.microservices.mlagenteval.dto;

import lombok.Data;

@Data
public class EvalDataSetInfo {
    private String scene;

    private String app;

    private String instruction;

    private String dag;

    private String stepNum;
}
