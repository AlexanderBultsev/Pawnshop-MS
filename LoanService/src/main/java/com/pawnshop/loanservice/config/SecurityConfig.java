package com.pawnshop.loanservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/loans/**").authenticated() // Защита всех эндпоинтов
                        .anyExchange().permitAll()) // Остальные запросы открыты
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())) // Использование JWT
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Отключение CSRF
                .build();
    }
}
