package com.young.microservices.mlagenteval.common;

import lombok.Data;

/**
 * 功能描述
 *
 * @author Young
 * @since 2024-12-07
 */
@Data
public class CommonResponse extends BaseResponse {
    private Object data;

    private Boolean success;

    /**
     * success response
     * @return CommonResponse
     */
    public static CommonResponse successResponse() {
        return successResponse(null);
    }

    /**
     * success response
     * @param data object
     * @return CommonResponse
     */
    public static CommonResponse successResponse(Object data) {
        CommonResponse baseResponse = new CommonResponse();
        baseResponse.setCode(SUCCESS_STATUS);
        baseResponse.setSuccess(Boolean.TRUE);
        baseResponse.setData(data);
        return baseResponse;
    }

    /**
     * failed response
     * @param status status
     * @param message error message
     * @return CommonResponse
     */
    public static CommonResponse failedResponse(Integer status, String message) {
        CommonResponse baseResponse = new CommonResponse();
        baseResponse.setCode(status);
        baseResponse.setSuccess(Boolean.FALSE);
        baseResponse.setMessage(message);
        return baseResponse;
    }

    /**
     * failed response
     * @param message error message
     * @return CommonResponse
     */
    public static CommonResponse failedResponse(String message) {
        CommonResponse baseResponse = new CommonResponse();
        baseResponse.setCode(FAILED_STATUS);
        baseResponse.setSuccess(Boolean.FALSE);
        baseResponse.setMessage(message);
        return baseResponse;
    }
}
