package com.lifengdi.gateway.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifengdi.gateway.constant.ContentType;
import com.lifengdi.gateway.constant.HeaderConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class LogHelper {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    private final static Map<String , String> FILE_CONTENT_TYPE = ContentType.getAllFile();

    /**
     * Log转JSON
     * @param dto Log
     * @return JSON字符串
     */
    public static String toJsonString(@NonNull Log dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            log.error("Log转换JSON异常", e);
            return null;
        }
    }

    /**
     * 根据MediaType获取字符集，如果获取不到，则默认返回<tt>UTF_8</tt>
     * @param mediaType MediaType
     * @return Charset
     */
    public static Charset getMediaTypeCharset(@Nullable MediaType mediaType) {
        if (Objects.nonNull(mediaType) && mediaType.getCharset() != null) {
            return mediaType.getCharset();
        } else {
            return StandardCharsets.UTF_8;
        }
    }

    /**
     * 记录日志（后期可扩展为通过MQ将日志发送到ELK系统）
     * @param dto Log
     * @return Mono.empty()
     */
    public static Mono<Void> doRecord(Log dto) {
        log.info(toJsonString(dto));
        return Mono.empty();
    }

    /**
     * 从HttpHeaders获取请求开始时间
     * <p>
     *     要求请求头中必须要有参数{@link HeaderConstant#START_TIME_KEY}，否则将返回当前时间戳
     * </p>
     * @param headers HttpHeaders请求头
     * @return 开始时间时间戳（Mills）
     */
    public static long getStartTime(HttpHeaders headers) {
        String startTimeStr = headers.getFirst(HeaderConstant.START_TIME_KEY);
        return StringUtils.isNotBlank(startTimeStr) ? Long.parseLong(startTimeStr) : System.currentTimeMillis();
    }

    /**
     * 根据HttpHeaders请求头获取请求执行时间
     * <p>
     *     要求请求头中必须要有参数{@link HeaderConstant#START_TIME_KEY}
     * </p>
     * @param headers HttpHeaders请求头
     * @return 请求执行时间
     */
    public static long getHandleTime(HttpHeaders headers) {
        String startTimeStr = headers.getFirst(HeaderConstant.START_TIME_KEY);
        long startTime = StringUtils.isNotBlank(startTimeStr) ? Long.parseLong(startTimeStr) : System.currentTimeMillis();
        return System.currentTimeMillis() - startTime;
    }

    /**
     * 读取请求体内容
     * @param request ServerHttpRequest
     * @return 请求体
     */
    public static String readRequestBody(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        MediaType mediaType = headers.getContentType();
        String method = request.getMethodValue().toUpperCase();
        if (Objects.nonNull(mediaType) && mediaType.equals(MediaType.MULTIPART_FORM_DATA)) {
            return "上传文件";
        } else {
            if (method.equals("GET")) {
                if (!request.getQueryParams().isEmpty()) {
                    return request.getQueryParams().toString();
                }
                return null;
            } else {
                AtomicReference<String> bodyString = new AtomicReference<>();
                request.getBody().subscribe(buffer -> {
                    byte[] bytes = new byte[buffer.readableByteCount()];
                    buffer.read(bytes);
                    DataBufferUtils.release(buffer);
                    bodyString.set(new String(bytes, getMediaTypeCharset(mediaType)));
                });
                return bodyString.get();
            }
        }
    }

    /**
     * 判断是否是上传文件
     * @param mediaType MediaType
     * @return Boolean
     */
    public static boolean isUploadFile(@Nullable MediaType mediaType) {
        if (Objects.isNull(mediaType)) {
            return false;
        }
        String mediaTypeStr = mediaType.toString();
        // 处理类似multipart/form-data; boundary=<calculated when request is sent>的情况
        mediaTypeStr = mediaTypeStr.split(";")[0];
        return mediaTypeStr.equals(MediaType.MULTIPART_FORM_DATA.toString())
                || mediaTypeStr.equals(MediaType.IMAGE_GIF.toString())
                || mediaTypeStr.equals(MediaType.IMAGE_JPEG.toString())
                || mediaTypeStr.equals(MediaType.IMAGE_PNG.toString())
                || mediaTypeStr.equals(MediaType.MULTIPART_MIXED.toString())
                || FILE_CONTENT_TYPE.containsValue(mediaTypeStr);
    }
}
