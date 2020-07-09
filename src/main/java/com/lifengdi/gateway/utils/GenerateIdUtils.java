package com.lifengdi.gateway.utils;

import java.util.UUID;

/**
 * ID生成工具类
 * @author: Li Fengdi
 * @date: 2020-03-17 15:26
 */
public class GenerateIdUtils {

    /**
     * 使用UUID生成RequestId
     * @return RequestId
     */
    public static String requestIdWithUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
