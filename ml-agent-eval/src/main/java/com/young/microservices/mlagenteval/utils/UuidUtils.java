package com.young.microservices.mlagenteval.utils;

import java.util.UUID;

public class UuidUtils {


    /**
     * 生成32位的UUID字符串。
     *
     * @return 返回32位的UUID字符串。
     */
    public static String genSimpleUuid() {
        // 生成标准的36位UUID字符串
        String uuid = UUID.randomUUID().toString();
        // 去掉"-"符号
        return uuid.replace("-", "");
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) {
        // 示例：打印5个32位的UUID
        for (int i = 0; i < 5; i++) {
            System.out.println(genSimpleUuid());
        }
    }
}
