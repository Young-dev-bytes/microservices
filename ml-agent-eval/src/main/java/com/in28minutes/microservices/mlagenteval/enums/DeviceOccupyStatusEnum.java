package com.in28minutes.microservices.mlagenteval.enums;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public enum DeviceOccupyStatusEnum {
    OCCUPIED("occupied","occupied"),
    NOOCCUPY("nooccupy","nooccupy");

    private String status;

    private String value;

    public String getValue() {
        return value;
    }
}
