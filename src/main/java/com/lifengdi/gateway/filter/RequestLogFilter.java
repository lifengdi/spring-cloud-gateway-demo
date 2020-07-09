package com.lifengdi.gateway.filter;

import com.lifengdi.gateway.constant.HeaderConstant;
import com.lifengdi.gateway.constant.OrderedConstant;
import com.lifengdi.gateway.log.Log;
import com.lifengdi.gateway.log.LogHelper;
import com.lifengdi.gateway.utils.GenerateIdUtils;
import com.lifengdi.gateway.utils.IpUtils;
import io.netty.buffer.UnpooledByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * 请求日志打印
 */
@Component
@Slf4j
public class RequestLogFilter implements GlobalFilter, Ordered {

    @Override
    public int getOrder() {
        return OrderedConstant.REQUEST_FILTER;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        long startTime = System.currentTimeMillis();
        try {
            ServerHttpRequest request = exchange.getRequest();
            // 设置X-Request-Id
            AtomicReference<String> requestId = new AtomicReference<>(GenerateIdUtils.requestIdWithUUID());
            Consumer<HttpHeaders> httpHeadersConsumer = httpHeaders -> {
                String headerRequestId = request.getHeaders().getFirst(HeaderConstant.REQUEST_ID);
                if (StringUtils.isBlank(headerRequestId)) {
                    httpHeaders.set(HeaderConstant.REQUEST_ID, requestId.get());
                } else {
                    requestId.set(headerRequestId);
                }
                httpHeaders.set(HeaderConstant.START_TIME_KEY, String.valueOf(startTime));
            };
            ServerRequest serverRequest = ServerRequest.create(exchange,
                    HandlerStrategies.withDefaults().messageReaders());
            URI requestUri = request.getURI();
            String uriQuery = requestUri.getQuery();
            String url = requestUri.getPath() + (StringUtils.isNotBlank(uriQuery) ? "?" + uriQuery : "");
            HttpHeaders headers = request.getHeaders();
            MediaType mediaType = headers.getContentType();
            String method = request.getMethodValue().toUpperCase();

            // 原始请求体
            final AtomicReference<String> requestBody = new AtomicReference<>();
            final AtomicBoolean newBody = new AtomicBoolean(false);
            if (Objects.nonNull(mediaType) && LogHelper.isUploadFile(mediaType)) {
                requestBody.set("上传文件");
            } else {
                if (method.equals("GET")) {
                    if (StringUtils.isNotBlank(uriQuery)) {
                        requestBody.set(uriQuery);
                    }
                } else {
                    newBody.set(true);
                }
            }
            final Log logDTO = new Log();
            logDTO.setLevel(Log.LEVEL.INFO);
            logDTO.setRequestUrl(url);
            logDTO.setRequestBody(requestBody.get());
            logDTO.setRequestMethod(method);
            logDTO.setRequestId(requestId.get());
            logDTO.setIp(IpUtils.getClientIp(request));

            ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate().headers(httpHeadersConsumer).build();
            ServerWebExchange build = exchange.mutate().request(serverHttpRequest).build();
            return build.getSession().flatMap(webSession -> {
                logDTO.setSessionId(webSession.getId());
                if (newBody.get() && headers.getContentLength() > 0) {
                    Mono<String> bodyToMono = serverRequest.bodyToMono(String.class);
                    return bodyToMono.flatMap(reqBody -> {
                        logDTO.setRequestBody(reqBody);
                        // 重写原始请求
                        ServerHttpRequestDecorator requestDecorator = new ServerHttpRequestDecorator(exchange.getRequest()) {
                            @Override
                            public Flux<DataBuffer> getBody() {
                                NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(new UnpooledByteBufAllocator(false));
                                DataBuffer bodyDataBuffer = nettyDataBufferFactory.wrap(reqBody.getBytes());
                                return Flux.just(bodyDataBuffer);
                            }
                        };
                        return chain.filter(exchange.mutate()
                                .request(requestDecorator)
                                .build()).then(LogHelper.doRecord(logDTO));
                    });
                } else {
                    return chain.filter(exchange).then(LogHelper.doRecord(logDTO));
                }
            });

        } catch (Exception e) {
            log.error("请求日志打印出现异常", e);
            return chain.filter(exchange);
        }
    }

}
