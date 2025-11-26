package com.exemplo.apifest.config;

import com.exemplo.apifest.interceptor.RateLimitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração Web da aplicação
 * Inclui interceptors, CORS, e outras configurações web
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private RateLimitInterceptor rateLimitInterceptor;

    /**
     * Registra interceptors da aplicação
     * Inclui rate limiting para todas as rotas da API
     */
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                    "/api/docs/**",
                    "/api/swagger-ui/**",
                    "/api/v3/api-docs/**",
                    "/actuator/**"
                );
    }
}