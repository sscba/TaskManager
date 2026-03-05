package com.taskmanager.taskmanagerapp.config.interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final RateLimitInterceptor rateLimitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor)
                // Apply rate limiting to all endpoints
                .addPathPatterns("/api/**")
                // Exclude Swagger UI (optional - you may want to rate limit these too)
                .excludePathPatterns("/swagger-ui/**", "/v3/api-docs/**");
    }

}
