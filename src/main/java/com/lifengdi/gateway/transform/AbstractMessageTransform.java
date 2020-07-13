package com.lifengdi.gateway.transform;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.Resource;

/**
 * 报文转换抽象类
 *
 * @author: Li Fengdi
 * @date: 2020-7-11 16:57:07
 */
public abstract class AbstractMessageTransform implements IMessageTransform {
    @Resource
    protected ObjectMapper objectMapper;

    /**
     * ResponseResult转JSON
     *
     * @param object 需要转换为json的对象
     * @return JSON字符串
     */
    public String toJsonString(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

}
