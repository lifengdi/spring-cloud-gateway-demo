package com.lifengdi.gateway.transform.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lifengdi.gateway.properties.entity.MessageTransformUrl;
import com.lifengdi.gateway.transform.AbstractMessageTransform;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * application/json类型转换实现类
 * @author: Li Fengdi
 * @date: 2020-7-11 16:57:07
 */
@Service
@Slf4j
public class JsonMessageTransformImpl extends AbstractMessageTransform {

    @Override
    public String transform(String originalContent, MessageTransformUrl transformUrl) {

        if (StringUtils.isBlank(originalContent)) {
            return originalContent;
        }

        try {
            // 原始报文转换为JsonNode
            JsonNode jsonNode = objectMapper.readTree(originalContent);

            List<String> fields = transformUrl.getFields();

            // 创建新的JSON对象
            ObjectNode rootNode = objectMapper.createObjectNode();
            fields.forEach(field -> {
                String[] fieldArray = field.split(":");
                String newFiled = fieldArray[0];
                String oldField = fieldArray.length > 1 ? fieldArray[1] : newFiled;
                if (jsonNode.has(oldField)) {
                    rootNode.set(newFiled, jsonNode.get(oldField));
                }
            });

            return toJsonString(rootNode);
        } catch (JsonProcessingException e) {
            log.error("application/json类型转换异常,originalContent:{},transformUrl:{}", originalContent, transformUrl);
            return originalContent;
        }
    }
}
