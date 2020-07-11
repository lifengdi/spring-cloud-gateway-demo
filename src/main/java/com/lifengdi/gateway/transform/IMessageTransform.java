package com.lifengdi.gateway.transform;

import com.lifengdi.gateway.properties.entity.MessageTransformUrl;

/**
 * 报文结构转换接口
 *
 * @author: Li Fengdi
 * @date: 2020-7-11 16:57:07
 */
public interface IMessageTransform {

    /**
     * 转换报文结构
     *
     * @param originalContent 需要转换的原始内容
     * @param transformUrl    MessageTransformUrl
     * @return 转换后的结构
     */
    String transform(String originalContent, MessageTransformUrl transformUrl);
}
