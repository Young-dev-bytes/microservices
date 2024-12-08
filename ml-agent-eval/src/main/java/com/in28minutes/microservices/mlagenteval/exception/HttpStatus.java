package com.in28minutes.microservices.mlagenteval.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HttpStatus {
    private int statusCode;
    private String reasonPhrase;
}
