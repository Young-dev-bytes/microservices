package com.young.microservices.mlagenteval.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Getter
public enum JobStatusEnum {
    PENDING("PENDING", "任务等待"),

    RUNNING("RUNNING", "正在执行"),

    FAILED("FAILED", "执行失败"),

    SHUTDOWN("STOPPED", "已终止"),

    SUCCESS("SUCCESS", "执行成功");

    private String value;

    private String desc;

    JobStatusEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }


    public static List<String> getEndStatus() {
        return Arrays.asList(FAILED.getValue(), SHUTDOWN.getValue(), SUCCESS.getValue());
    }

    public static boolean endStatus(String status) {
        return getEndStatus().contains(status);
    }

    public static String getByValue(String value) {
        Optional<JobStatusEnum> optional = Arrays.stream(JobStatusEnum.values())
                .filter(item -> item.getValue().equalsIgnoreCase(value))
                .findFirst();
        return optional.map(JobStatusEnum::getDesc).orElse(null);
    }

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}
