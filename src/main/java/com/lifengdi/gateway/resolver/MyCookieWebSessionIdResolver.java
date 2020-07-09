package com.lifengdi.gateway.resolver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.session.CookieWebSessionIdResolver;

import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义WebSessionId解析器，以兼容{@link org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession}
 * <p>
 * 使用EnableRedisHttpSession时{@link DefaultCookieSerializer}中useBase64Encoding默认为true，将cookie中的sessionId使用base64
 * 加密，但是如果使用{@link org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession}，默认
 * 的解析器没有将sessionId解密，导致获取不到正确的session
 * </p>
 *
 * @author: Li Fengdi
 * @date: 2020/3/16 15:41
 */
@Slf4j
public class MyCookieWebSessionIdResolver extends CookieWebSessionIdResolver {

    @Override
    public List<String> resolveSessionIds(ServerWebExchange exchange) {
        MultiValueMap<String, HttpCookie> cookieMap = exchange.getRequest().getCookies();
        List<HttpCookie> cookies = cookieMap.get(getCookieName());
        if (cookies == null) {
            return Collections.emptyList();
        }
        return cookies.stream().map(HttpCookie::getValue).map(this::base64Decode).collect(Collectors.toList());
    }

    /**
     * base64解码
     *
     * @param base64Value base64Value
     * @return 解码后的字符串
     */
    private String base64Decode(String base64Value) {
        try {
            byte[] decodedCookieBytes = Base64.getDecoder().decode(base64Value);
            return new String(decodedCookieBytes);
        } catch (Exception ex) {
            log.debug("Unable to Base64 decode value: " + base64Value);
            return null;
        }
    }

}
