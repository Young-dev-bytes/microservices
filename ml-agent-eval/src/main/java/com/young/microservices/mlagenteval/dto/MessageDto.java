package com.young.microservices.mlagenteval.dto;

import lombok.Data;

@Data
public class MessageDto {
    private Object data;

    private String messageType;
}
