package com.lifengdi.gateway.transform;

import com.lifengdi.gateway.enums.TransformContentTypeEnum;
import com.lifengdi.gateway.properties.MessageTransformProperties;
import com.lifengdi.gateway.properties.entity.MessageTransformUrl;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 报文结构转换工厂类
 *
 * @author: Li Fengdi
 * @date: 2020-7-11 16:57:07
 */
@Component
public class MessageTransformFactory {

    @Resource
    private Map<String, AbstractMessageTransform> messageTransformMap;

    @Resource
    private MessageTransformProperties messageTransformProperties;

    /**
     * 根据contentType获取对应的内容转换实现类
     *
     * @param contentType 内容类型
     * @return 内容转换实现类
     */
    private AbstractMessageTransform getMessageTransform(String contentType) {
        return messageTransformMap.get(TransformContentTypeEnum.getWithDefault(contentType).getTransImpl());
    }

    /**
     * 报文转换
     *
     * @param originalContent 原始内容
     * @param transformUrl    url
     * @return 转换后的消息
     */
    private String messageTransform(String originalContent, MessageTransformUrl transformUrl) {
        String contentType = transformUrl.getContentType();
        AbstractMessageTransform messageTransform = getMessageTransform(contentType);

        return messageTransform.transform(originalContent, transformUrl);
    }

    /**
     * 判断是否是需要转换报文结构的接口，如果是则转换，否则返回原值
     *
     * @param path            接口路径
     * @param originalContent 原始内容
     * @return 转换后的内容
     */
    public String compareAndTransform(String path, String originalContent) {
        if (StringUtils.isBlank(originalContent)) {
            return null;
        }
        List<MessageTransformUrl> urlList = messageTransformProperties.getUrlList();
        if (CollectionUtils.isEmpty(urlList)) {
            return originalContent;
        }
        return urlList .stream()
                .filter(transformUrl -> transformUrl.getPathList().contains(path))
                .findFirst()
                .map(url -> messageTransform(originalContent, url))
                .orElse(originalContent);
    }

    /**
     * 判断是否是需要转换报文结构的接口，如果是则转换，否则返回原值
     *
     * @param path              接口路径
     * @param originalContent   原始内容
     * @param originalByteArray 二进制原始内容
     * @param charset           charset
     * @param newResponseBody   新报文内容
     * @return 响应体数组数组
     */
    public byte[] compareAndTransform(String path, String originalContent, byte[] originalByteArray, Charset charset,
                                      AtomicReference<String> newResponseBody) {
        if (StringUtils.isBlank(originalContent)) {
            return null;
        }
        List<MessageTransformUrl> urlList = messageTransformProperties.getUrlList();
        if (CollectionUtils.isEmpty(urlList)) {
            return originalByteArray;
        }
        return urlList.stream()
                .filter(transformUrl -> transformUrl.getPathList().contains(path))
                .findFirst()
                .map(url -> {
                    String messageTransform = messageTransform(originalContent, url);
                    if (originalContent.equals(messageTransform)) {
                        return originalByteArray;
                    }
                    newResponseBody.set(messageTransform);
                    return messageTransform.getBytes(charset);
                })
                .orElse(originalByteArray);
    }
}
