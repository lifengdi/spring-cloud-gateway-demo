package com.lifengdi.gateway.enums;

import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.springframework.lang.Nullable;

/**
 * 报文结构转换转换类型枚举类
 *
 * @author: Li Fengdi
 * @date: 2020-7-11 16:57:07
 */
@Getter
public enum TransformContentTypeEnum {

    DEFAULT(null, "jsonMessageTransformImpl")
    , APPLICATION_JSON("application/json", "jsonMessageTransformImpl")
    ;
    /**
     * 内容类型
     */
    private String contentType;

    /**
     * 报文转换结构实现类
     */
    private String transImpl;

    TransformContentTypeEnum(String contentType, String transImpl) {
        this.contentType = contentType;
        this.transImpl = transImpl;
    }

    /**
     * 根据contentType获取对应枚举
     * <p>
     * 如果contentType为空则返回默认枚举
     * </p>
     *
     * @param contentType contentType
     * @return TransformContentTypeEnum
     */
    public static TransformContentTypeEnum getWithDefault(@Nullable String contentType) {
        if (StringUtils.isNotBlank(contentType)) {
            for (TransformContentTypeEnum transformContentTypeEnum : values()) {
                if (contentType.equals(transformContentTypeEnum.contentType)) {
                    return transformContentTypeEnum;
                }
            }
        }
        return DEFAULT;
    }
}
