package com.in28minutes.microservices.mlagenteval.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum MessageWsEnum {

    QUERY_EVAL_LOG_MESSAGE("QUERY_EVAL_LOG", "query eval log");

    private final String value;
    private final String desc;

    MessageWsEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static MessageWsEnum getByValue(String value) {
        Optional<MessageWsEnum> optional = Arrays.stream(MessageWsEnum.values()).filter(item -> item.getValue().equalsIgnoreCase(value)).findFirst();
        return optional.isPresent() ? optional.get() : null;
    }
}
