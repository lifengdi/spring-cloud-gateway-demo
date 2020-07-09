package com.lifengdi.gateway.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ApiException
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ApiException extends RuntimeException {

    private String code;

    private String msg;

    public ApiException(String code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
}
