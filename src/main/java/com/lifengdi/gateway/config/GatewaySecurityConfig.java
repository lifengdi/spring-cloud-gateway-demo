package com.lifengdi.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {
    @Bean
    SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity serverHttpSecurity)
            throws Exception {
        serverHttpSecurity
                .csrf().disable()
                .authorizeExchange().pathMatchers("/**").permitAll()
                .anyExchange()
                .authenticated();
        return serverHttpSecurity.build();
    }
}
