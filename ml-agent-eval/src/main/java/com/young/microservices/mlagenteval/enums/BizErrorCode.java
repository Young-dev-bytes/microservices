/*
 * Copyright (c) Honor Device Co., Ltd. 2021-2021. All rights reserved.
 */

package com.young.microservices.mlagenteval.enums;



import javax.ws.rs.core.Response;

/**
 * 功能描述
 *
 * @author xw0051920
 * @since 2021-10-18
 */
public enum BizErrorCode implements ErrorCode {
    /**
     * 请求kubeflow失败
     */
    INVOKE_KUBEFLOW_ERROR("102100100001", "Invoke kubeflow failed."),
    /**
     * 访问kubeflow超时
     */
    KUBEFLOW_RESPONSE_SIZE_EXCEED_LIMIT("102100100004", "Size of kubeflow response exceed limit."),

    /**
     * HTTP响应失败
     */
    HTTP_RESPONSE_ERROR("102100100002", "http response error."),

    /**
     * 访问kubeflow超时
     */
    INVOKE_KUBEFLOW_TIMEOUT("102100100003", "Invoke timeout."),
    /**
     * 请选择租户
     */
    TENANT_NOT_EXIST("102102101001", "Please select tenant. "),
    /**
     * 请选择项目
     */
    PROJECT_NOT_EXIST("102102101002", "Please select project. "),

    /**
     * 获取租户信息失败
     */
    TENANT_ID_NOT_FOUND("102102101003", "Obtaining tenantId information is abnormal"),

    /**
     * 用户没有权限
     */
    USER_NOT_PERMISSION("102102101004", "User does not have permission"),

    /**
     * REST_REQUEST_NOT_SUCCESS
     */
    REST_REQUEST_NOT_SUCCESS("102102101005", "Post request failed"),

    /**
     * REST_REQUEST_UNKNOWN_ERROR
     */
    REST_REQUEST_UNKNOWN_ERROR("102102101006", "Rest request unknown error"),

    /**
     * 获取用户信息失败
     */
    USER_NOT_FOUND("102102101007", "Obtaining user information is abnormal"),
    /**
     * server error
     */
    SERVER_ERROR("102102101008", "Server error."),

    /**
     * param invalid
     */
    PARAM_INVALID("102102101009", "Param invalid."),
    /**
     * Send email failed
     */
    SEND_EMAIL_ERROR("102102101010", "Send email failed."),
    /**
     * 数据不存在
     */
    DATA_NOT_EXIST("102102101011", "Data not exist."),
    /**
     * 功能未支持
     */
    OPERATION_NOT_SUPPORT("102102101012", "Operation not support now."),
    /**
     * duplicate operation
     */
    DUPLICATE_OPERATION("102102101013", "Operation is duplicated."),
    /**
     * 没有权限操作
     */
    NO_PERMISSION_OPERATE("102102101014", "No permission to operate."),

    /**
     * download nfs
     */
    NFS_DOWNLOAD_FILE_ERROR("102102101015", "nfs download file error."),

    /**
     * upload nfs
     */
    NFS_UPLOAD_FILE_ERROR("102102101016", "nfs upload file error."),

    /**
     * remove nfs
     */
    NFS_REMOVE_FILE_ERROR("102102101017", "nfs remove file error."),

    /**
     * nfs file
     */
    NFS_FILE_NOT_EXIST_ERROR("102102101018", "nfs file not exist error."),

    /**
     * Obs file download failed.
     */
    OBS_FILE_DOWNLOAD_FAILED("102102101019", "Obs file download failed."),

    /**
     * nfs file type error
     */
    NFS_FILE_TYPE_ERROR("102102101020", "nfs file type error."),

    /**
     * Check file Sha256 failed.
     */
    CHECK_FILE_SHA256_FAILED("102102101021", "Check file Sha256 failed."),
    /**
     * File write failed.
     */
    FILE_WRITE_FAILED("102102101022", "File write failed."),
    /**
     * can not find cluster config.
     */
    TENANT_CLUSTER_INFO_NOT_EXIST("102102101023", "can not find cluster config."),
    /**
     * can not find nas config.
     */
    TENANT_NAS_INFO_NOT_EXIST("102102101024", "can not find nas config."),
    /**
     * build cluster client error
     */
    BUILD_CLUSTER_CLIENT_ERROR("102102101025", "build cluster client error"),
    /**
     * File format is not supported
     */
    FILE_NOT_SUPPORT("102102101026", "File format is not supported"),
    /**
     * File too large
     */
    FILE_IS_LARGE("102102101027", "File is too large, please download"),
    /**
     * Obs file download failed.
     */
    OBS_CONFIG_ERROR("102102101028", "Obs config error."),
    /**
     * job already exists
     */
    JOB_HAS_EXISTS("102102101029", "A task with the same name already exists"),

    /**
     * not found corresponds tenant for the tenantCode
     */
    GPFS_TENANT_NOT_FOUND("102102101030", "not found corresponds tenant for the tenantCode"),

    /**
     * create gpfs path error
     */
    GPFS_PATH_CREATE_ERROR("102102101031", "failed to create gpfs path"),
    /**
     * create cfs path error
     */
    CFS_PATH_CREATE_ERROR("102102101032", "failed to create cfs path"),
    /*************** 7-9位 102： scheduler proxy相关错误码 ************************/
    /**
     * magic scheduler server error
     */
    SCHEDULER_SERVER_ERROR("102102102001", "magic scheduler server error"),
    /**
     * data computation server error
     */
    DATA_COMPUTATION_SERVER_ERROR("102102102002", "data computation server error."),
    /*************** 7-9位 103： file-cache相关错误码 ************************/
    /**
     * 不支持的缓存类型
     */
    NOT_SUPPORTED_MEDIUM_TYPE_ERROR("102100103001", "Not Support medium type."),
    /**
     * 预加载状态异常
     */
    FLUID_DATALOAD_STATUS_ERROR("102100103002", "fluid dataload status error."),
    /**
     * 线程池创建失败
     */
    THREAD_POOL_CREATE_ERROR("102100104001", "线程池创建失败");


    private final String resultCode;

    private final String resultDesc;

    BizErrorCode(String resultCode, String resultDesc) {
        this.resultCode = resultCode;
        this.resultDesc = resultDesc;
    }

    public String getResultDesc() {
        return resultDesc;
    }

    public String getResultCode() {
        return resultCode;
    }

    @Override
    public String getCode() {
        return resultCode;
    }

    @Override
    public String getMessage() {
        return resultDesc;
    }

    @Override
    public Response.Status getHttpStatus() {
        return Response.Status.NOT_ACCEPTABLE;
    }
}
