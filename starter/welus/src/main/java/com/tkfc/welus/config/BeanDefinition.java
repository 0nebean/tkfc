package com.tkfc.welus.config;

import com.tkfc.welus.interceptor.AuthorizationInterceptor;
import com.tkfc.welus.resolver.BodyParamResolver;
import com.tkfc.welus.resolver.PathParamResolver;
import com.tkfc.welus.resolver.UrlParamResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Bean定义类
 *
 * @author 0neBean
 * @version 1.0
 * @since 2024-08-28 17:02:59
 */
@Configuration
public class BeanDefinition {

    @Bean
    public UrlParamResolver urlParamResolver() {
        return new UrlParamResolver();
    }

    @Bean
    public PathParamResolver pathParamResolver() {
        return new PathParamResolver();
    }

    @Bean
    public BodyParamResolver bodyParamResolver() {
        return new BodyParamResolver();
    }

    @Bean
    public AuthorizationInterceptor authorizationInterceptor() {
        return new AuthorizationInterceptor();
    }
}
