package com.lifengdi.gateway.properties;

import com.lifengdi.gateway.properties.entity.MessageTransformUrl;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 报文结构转换参数配置
 * @author: Li Fengdi
 * @date: 2020-7-11 16:55:53
 */
@Component
@ConfigurationProperties(prefix = "trans")
@Data
public class MessageTransformProperties {

    private List<MessageTransformUrl> urlList;

}
