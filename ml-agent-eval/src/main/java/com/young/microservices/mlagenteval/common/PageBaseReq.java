package com.young.microservices.mlagenteval.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageBaseReq {
    private static final int CUR_PAGE = 1;

    private static final int PAGE_SIZE = 15;

    private int curPage = CUR_PAGE;

    private int pageSize = PAGE_SIZE;
}
