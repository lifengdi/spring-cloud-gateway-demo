package com.lifengdi.gateway.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BaseException implements ApiExceptionFactory {

    OPERATE_ERROR("0", "操作失败"),
    UNAUTHORIZED("401", "登陆信息失效，请重新登陆!", HttpStatus.UNAUTHORIZED),
    DEFAULT_HYSTRIX("500", "请求超时。"),
    ;

    private String code;

    private String msg;

    private HttpStatus httpStatus;

    @Override
    public String prefix() {
        return null;
    }

    BaseException(String code, String msg) {
        this(code, msg, HttpStatus.OK);
    }

    BaseException(String code, String msg, HttpStatus httpStatus) {
        this.code = code;
        this.msg = msg;
        this.httpStatus = httpStatus;
    }

    /**
     * 构建异常信息
     * @return ApiException
     */
    public ApiException build() {
        return apply(code, msg);
    }

    /**
     * 构建异常信息（在异常信息之后追加自定义信息）
     * @param message 自定义信息
     * @return ApiException
     */
    public ApiException build(String message) {
        return apply(code, msg + message);
    }

    /**
     * 构建异常信息（格式化信息）
     * @param messages 需要填充的信息
     * @return ApiException
     */
    public ApiException builds(Object ...messages) {
        return apply(code, String.format(msg, messages));
    }
}
