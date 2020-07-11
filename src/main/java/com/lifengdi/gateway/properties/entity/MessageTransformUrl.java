package com.lifengdi.gateway.properties.entity;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 需要转换报文结构的URL地址配置类
 *
 * @author: Li Fengdi
 * @date: 2020-7-11 16:57:07
 */
@Data
public class MessageTransformUrl {

    // 接口地址，多个地址使用英文逗号分隔
    private String[] paths;

    /**
     * <p>格式</p>
     * <p>新字段:老字段</p>
     * <p>若新老字段一致，可以只配置新字段</p>
     */
    private List<String> fields;

    /**
     * <p>返回体类型，默认为json </p>
     * <p>可配置的类型参见{@link com.lifengdi.gateway.enums.TransformContentTypeEnum}</p>
     * <p>如需自定义配置，可以继承{@link com.lifengdi.gateway.transform.AbstractMessageTransform}类，
     * 或者实现{@link com.lifengdi.gateway.transform.IMessageTransform}接口类，重写transform方法</p>
      */
    private String contentType;

    private Set<String> pathList;

    public Set<String> getPathList() {
        if (CollectionUtils.isEmpty(pathList) && Objects.nonNull(paths)) {
            setPathList(new HashSet<>(Arrays.asList(paths)));
        }
        return pathList;
    }
}
