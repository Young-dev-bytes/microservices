package com.young.microservices.mlagenteval.dto;

import com.young.microservices.mlagenteval.common.BaseResponse;
import lombok.Data;

import java.util.List;

@Data
public class PageBaseResponse<T> extends BaseResponse {
    private long currentPage;

    private long total;

    private long pageSize;

    private List<T> records;

    /**
     * PageBaseResponse
     */
    public PageBaseResponse() {
        currentPage = 1;
        pageSize = 15;
    }

    /**
     * PageBaseResponse
     * @param current current
     * @param size size
     * @param total total
     */
    public PageBaseResponse(long current, long size, long total) {
        currentPage = current;
        this.total = total;
        pageSize = size;
    }
}
