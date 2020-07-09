package com.lifengdi.gateway.enums;

import lombok.Getter;

/**
 * 响应码枚举
 * @author: Li Fengdi
 * @date: 2020-03-18 16:36
 */
@Getter
public enum ResultCodeEnum {

    SUCCESS("200", "SUCCESS"),
    BAD_REQUEST("400", "BAD_REQUEST"),
    FAIL("500", "FAIL"),
    ;

    private String code;

    private String msg;

    ResultCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
