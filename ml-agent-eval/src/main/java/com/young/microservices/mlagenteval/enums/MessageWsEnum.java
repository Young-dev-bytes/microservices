package com.young.microservices.mlagenteval.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum MessageWsEnum {

    QUERY_EVAL_LOG_MESSAGE("QUERY_EVAL_LOG", "query eval log"),
    QUERY_SCREEN_BINARY_MESSAGE("QUERY_SCREEN_BINARY", "query screen binary"),;

    private String value;

    private String desc;

    MessageWsEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static MessageWsEnum getByValue(String value) {
        Optional<MessageWsEnum> optional = Arrays.stream(MessageWsEnum.values())
                .filter(item -> item.getValue().equalsIgnoreCase(value))
                .findFirst();
        return optional.isPresent() ? optional.get() : null;
    }

}
