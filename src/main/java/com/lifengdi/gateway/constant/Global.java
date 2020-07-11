package com.lifengdi.gateway.constant;

import com.lifengdi.gateway.utils.EnvironmentUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class Global {

    @Value("${spring.application.name}")
    public String applicationName;

    @Value("${info.env:dev}")
    public String env;

    @Value("${authExcludeUrl:null}")
    public String authExcludeUrl;

    private List<String> excludeUrlList;

    /**
     * 初始化静态参数
     * @return 0 无实意
     */
    @Bean
    public int initStatic() {
        EnvironmentUtils.setApplicationName(applicationName);
        EnvironmentUtils.setEnv(env);
        excludeUrlList = Arrays.asList(authExcludeUrl.split(","));
        return 0;
    }

    /**
     * 获取需要排除的URL的列表
     * @return list
     */
    public List<String> getExcludeUrlList() {
        return excludeUrlList;
    }

}
