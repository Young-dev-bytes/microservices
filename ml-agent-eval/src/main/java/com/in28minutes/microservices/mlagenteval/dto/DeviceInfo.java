package com.in28minutes.microservices.mlagenteval.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class DeviceInfo {
    private String deviceName;

    @NotBlank
    private String deviceUdid;

    private String deviceOccupyStatus;

    private String deviceRunning;

    private String timeout;

    private String occupyUser;

    private LocalDateTime occupyTime;
}
