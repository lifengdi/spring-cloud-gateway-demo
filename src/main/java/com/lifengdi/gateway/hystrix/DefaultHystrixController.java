package com.lifengdi.gateway.hystrix;

import com.lifengdi.gateway.exception.BaseException;
import com.lifengdi.gateway.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: Li Fengdi
 * @date: 2020-03-18 16:35
 */
@RestController
@Slf4j
public class DefaultHystrixController {
    @RequestMapping("/fallback")
    public ResponseResult<Object> fallback(){

        log.error("触发熔断......");
        return ResponseResult.fail(BaseException.DEFAULT_HYSTRIX.build());
    }
}
