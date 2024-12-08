package com.in28minutes.microservices.mlagenteval.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;

public class JsonUtils {

    static {
        // 确保自动类型转换是安全的
        ParserConfig.getGlobalInstance().setAutoTypeSupport(false);
    }

    /**
     * 将给定的 JSON 字符串解析为指定类型的对象。
     *
     * @param jsonString 要解析的 JSON 字符串
     * @param clazz      目标对象的类型
     * @param <T>        泛型类型参数
     * @return 解析后的对象
     * @throws RuntimeException 如果解析失败，则抛出运行时异常
     */
    public static <T> T parseObject(String jsonString, Class<T> clazz) {
        try {
            return JSON.parseObject(jsonString, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON string to " + clazz.getName() + ".", e);
        }
    }

    /**
     * 将给定的对象序列化为 JSON 字符串。
     *
     * @param object 要序列化的对象
     * @return 序列化后的 JSON 字符串
     */
    public static String toJSONString(Object object) {
        try {
            return JSON.toJSONString(object);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize object to JSON string.", e);
        }
    }

    // 可以根据需要添加更多辅助方法...
}
