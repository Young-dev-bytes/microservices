package com.in28minutes.microservices.mlagenteval.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class HttpUtil {

    private static final RestTemplate restTemplate = new RestTemplate();

    /**
     * 发送 POST 请求到指定 URL 并返回响应体。
     *
     * @param url      目标 URL
     * @param request  请求体对象
     * @param responseType 响应体的目标类型
     * @param <T>      泛型类型参数
     * @return 解析后的响应体对象
     */
    public static <T> T post(String url, Object request, Class<T> responseType) {
        // 创建 HTTP 头信息
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 封装请求体和头信息
        HttpEntity<Object> entity = new HttpEntity<>(request, headers);

        // 发送 POST 请求并接收响应
        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.POST, entity, responseType);

        // 返回响应体
        return response.getBody();
    }
}
