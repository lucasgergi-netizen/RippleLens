package com.RippleLens.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.RippleLens.security.RequestSecurityInterceptor;

@Configuration
public class WebSecurityConfig implements WebMvcConfigurer {

    private final RequestSecurityInterceptor requestSecurityInterceptor;

    public WebSecurityConfig(RequestSecurityInterceptor requestSecurityInterceptor) {
        this.requestSecurityInterceptor = requestSecurityInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestSecurityInterceptor).addPathPatterns("/api/**");
    }
}
