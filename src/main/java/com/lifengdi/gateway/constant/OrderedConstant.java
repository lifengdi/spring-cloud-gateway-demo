package com.lifengdi.gateway.constant;

import org.springframework.core.Ordered;

import static org.springframework.cloud.gateway.filter.NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER;

/**
 * filter排序码
 */
public interface OrderedConstant extends Ordered {

    /**
     * 日志记录
     */
    int LOGGING_FILTER = WRITE_RESPONSE_FILTER_ORDER - 1;

    /**
     * request
     */
    int REQUEST_FILTER = HIGHEST_PRECEDENCE;

}
