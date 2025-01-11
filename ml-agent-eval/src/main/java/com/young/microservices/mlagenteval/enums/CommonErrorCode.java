package com.young.microservices.mlagenteval.enums;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public enum CommonErrorCode implements ErrorCode {
    BAD_REQUEST("100100100100", "Bad request."),
    UNAUTHORIZED("100100100101", "Authentication failure."),
    FORBIDDEN("100100100102", "Forbidden."),
    RESOURCE_NOT_FOUND("100100100103", "Resource not found"),
    TO_MANY_REQUEST("100100100104", "To many requests, retry later."),
    SERVER_INNER_ERROR("100100100105", "Server internal error."),
    JSON_PARSE_ERROR("100100100106", "Exception in jsonUtils."),
    HTTP_RESPONSE_SIZE_EXCEED_LIMIT("100100100107", "Size of http response exceed limit."),
    DATA_ACCESS_ERROR("100100100108", "database error"),
    XML_PARSE_ERROR("100100100109", "Exception in xmlUtils."),
    ENCRYPT_INIT_ERROR("100100100201", "encrypt init failed"),
    AES_GCM_ENCRYPT_ERROR("100100100202", "aes-gcm encrypt failed"),
    AES_GCM_DECRYPT_ERROR("100100100203", "aes-gcm decrypt failed"),
    CLOSE_STREAM_FAILED("100100100204", "close stream failed"),
    SM_ENCRYPT_FAILED("100100100205", "sm encrypt failed"),
    SM_DECRYPT_FAILED("100100100206", "sm decrypt failed"),
    CONFIG_CENTER_INSTANCE_FAILED("100100100207", "configcenter instance failed"),
    MONGO_AUTH_FAILED_INIT_ERROR("100100100301", "mongodb auth failed, init error"),
    MONGO_CREATE_DB_NOT_EXISTS_ERROR("100100100302", "mongodb init failed,dbName not exists"),
    MONGO_NO_DATASOURCE_EXISTS("100100100303", "mongodb oper failed, no datasource exists"),
    HTTP_FORM_REQUEST_PARSE_ERROR("100100100401", "http form request parse error"),
    ES_CLIENT_CHECK_HTTPS_CERT_NOT_EXISTS("100100100501", "使用https协议缺少证书"),
    ES_CLIENT_CHECK_PROTOCOL_NOT_HTTPS("100100100502", "使用证书情况下，请使用https协议"),
    ES_CLIENT_CHECK_AUTH_CONFIG_ERROR("100100100503", "URL中用户名或密码未按照规定配置"),
    ES_CLIENT_CHECK_CHECK_HEALTH_NOT_EXISTS("100100100504", "checkHealth参数未配置"),
    ES_CLIENT_CHECK_PRINT_CONTENT_NOT_EXISTS("100100100505", "printContent参数未配置"),
    ES_CLIENT_SECURITY_CERT_READ_IO_ERROR("100100100511", "证书读取IO异常"),
    ES_CLIENT_SECURITY_CERT_LOAD_ERROR("100100100512", "证书加载异常"),
    ES_CLIENT_SECURITY_CERT_ALGORITHM_NOT_EXISTS("100100100513", "不存在适用于该证书的算法"),
    ES_CLIENT_SECURITY_CERT_KEYSTORE_ERROR("100100100514", "keystore异常"),
    ES_CLIENT_SECURITY_CERT_KEY_MANAGE_ERROR("100100100515", "key管理异常"),
    ES_CLIENT_DATASOURCE_NOT_EXISTS("100100100521", "es数据源不存在");

    private final String code;
    private final String message;
    private final Response.Status httpStatus;

    private CommonErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
        this.httpStatus = Status.NOT_ACCEPTABLE;
    }

    private CommonErrorCode(String code, String message, Response.Status httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public Response.Status getHttpStatus() {
        return this.httpStatus;
    }
}
