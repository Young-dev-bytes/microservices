package com.in28minutes.microservices.mlagenteval.dto;

import lombok.Data;

@Data
public class MessageDto {
    private Object data;

    private String messageType;
}
