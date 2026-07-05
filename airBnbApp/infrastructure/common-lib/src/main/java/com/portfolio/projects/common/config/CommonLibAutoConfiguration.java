package com.portfolio.projects.common.config;

import com.portfolio.projects.common.exception.GlobalExceptionHandler;
import com.portfolio.projects.common.security.JWTAuthFilter;
import com.portfolio.projects.common.security.JWTService;
import com.portfolio.projects.common.security.WebSecurityConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({
        ModelMapperConfig.class,
        GlobalExceptionHandler.class,
        JWTAuthFilter.class,
        JWTService.class,
        WebSecurityConfig.class
})
public class CommonLibAutoConfiguration {
}
