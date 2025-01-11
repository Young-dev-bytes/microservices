package com.young.microservices.mlagenteval.utils;


import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.base.Preconditions;
import com.young.microservices.mlagenteval.enums.CommonErrorCode;
import com.young.microservices.mlagenteval.exception.ServerInnerException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Slf4j
public class JsonUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonUtils() {
    }

    public static String toJsonString(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException var2) {
            log.error("Write object as json string error.", var2);
            throw new ServerInnerException(CommonErrorCode.JSON_PARSE_ERROR);
        }
    }

    public static <T> T parseObject(String jsonStr, Class<T> valueType) {
        Preconditions.checkNotNull(valueType, "Object type can not be null.");

        try {
            return MAPPER.readValue(jsonStr, valueType);
        } catch (JsonProcessingException var3) {
            log.error("Parse object error.", var3);
            throw new ServerInnerException(CommonErrorCode.JSON_PARSE_ERROR);
        }
    }

    public static <T> T parseObject(InputStream src, Class<T> valueType) {
        try {
            return MAPPER.readValue(src, valueType);
        } catch (IOException var3) {
            log.error("Parse object from inputStream error.", var3);
            throw new ServerInnerException(CommonErrorCode.JSON_PARSE_ERROR);
        }
    }

    public static <E, T> Map<E, T> parseMap(String jsonStr, Class<?> keyClass, Class<?> valueClass) {
        try {
            MapLikeType mapLikeType = MAPPER.getTypeFactory().constructMapLikeType(Map.class, keyClass, valueClass);
            return (Map)MAPPER.readValue(jsonStr, mapLikeType);
        } catch (JsonProcessingException var4) {
            log.error("Parse map error.", var4);
            throw new ServerInnerException(CommonErrorCode.JSON_PARSE_ERROR);
        }
    }

    public static <T> List<T> parseList(String jsonStr, Class<T> elementClass) {
        try {
            JavaType type = MAPPER.getTypeFactory().constructCollectionType(List.class, elementClass);
            return (List)MAPPER.readValue(jsonStr, type);
        } catch (JsonProcessingException var3) {
            log.error("Parse list error.", var3);
            throw new ServerInnerException(CommonErrorCode.JSON_PARSE_ERROR);
        }
    }

    public static String removePretty(String prettyJsonStr) {
        if (StringUtils.isNotEmpty(prettyJsonStr)) {
            try {
                JsonNode jsonNode = (JsonNode)MAPPER.readValue(prettyJsonStr, JsonNode.class);
                return jsonNode.toString();
            } catch (JsonProcessingException var3) {
                log.error("removePretty failed", var3);
                throw new ServerInnerException(CommonErrorCode.JSON_PARSE_ERROR);
            }
        } else {
            return prettyJsonStr;
        }
    }

    public static ObjectNode parseObjectNode(String jsonStr) {
        try {
            return (ObjectNode)MAPPER.readValue(jsonStr, ObjectNode.class);
        } catch (JsonProcessingException var2) {
            log.error("Parse jsonNode error.", var2);
            throw new ServerInnerException(CommonErrorCode.JSON_PARSE_ERROR);
        }
    }

    public static <T, K, V> T convertMapToObject(Map<K, V> map, Class<T> targetClass) {
        try {
            return MAPPER.convertValue(map, targetClass);
        } catch (IllegalArgumentException var3) {
            log.error("Convert map to object error.", var3);
            throw new ServerInnerException(CommonErrorCode.JSON_PARSE_ERROR);
        }
    }

    static {
        MAPPER.disable(new JsonParser.Feature[]{Feature.AUTO_CLOSE_SOURCE});
        MAPPER.enable(new JsonParser.Feature[]{JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature()});
        MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        MAPPER.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        MAPPER.disable(new MapperFeature[]{MapperFeature.DEFAULT_VIEW_INCLUSION});
        MAPPER.enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
        MAPPER.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        MAPPER.setSerializationInclusion(Include.NON_NULL);
        MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        MAPPER.registerModule(new JavaTimeModule());
    }
}
