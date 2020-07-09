package com.lifengdi.gateway.exception;

import org.apache.commons.lang.StringUtils;

/**
 * ApiExceptionFactory
 */
public interface ApiExceptionFactory {
    String prefix();

    default ApiException apply(String code, String msg) {
        String prefix = prefix();
        prefix = StringUtils.isBlank(prefix) ? "" : prefix;
        return new ApiException(prefix + code, msg);
    }

}
