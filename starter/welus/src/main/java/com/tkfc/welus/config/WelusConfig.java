package com.tkfc.welus.config;

import com.tkfc.welus.handler.ResponseViewHandler;
import com.tkfc.welus.interceptor.AuthorizationInterceptor;
import com.tkfc.welus.listener.WelusReadyListener;
import com.tkfc.welus.resolver.BodyParamResolver;
import com.tkfc.welus.resolver.PathParamResolver;
import com.tkfc.welus.resolver.UrlParamResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Welus 配置
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/1/12 9:54
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class WelusConfig implements WebMvcConfigurer {

    private final UrlParamResolver urlParamResolver;
    private final PathParamResolver pathParamResolver;
    private final BodyParamResolver bodyParamResolver;
    private final AuthorizationInterceptor authorizationInterceptor;


    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        log.info("WebMvcConfig load custom param resolver...");
        //加载参数解析器
        resolvers.add(urlParamResolver);
        log.info("load custom url param resolver");
        resolvers.add(pathParamResolver);
        log.info("load custom path param resolver");
        resolvers.add(bodyParamResolver);
        log.info("load custom body param resolver");

        WebMvcConfigurer.super.addArgumentResolvers(resolvers);
    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> handlers) {
        // 使用 Spring MVC 标准扩展点注册，避免依赖 RequestMappingHandlerAdapter 内部 handler 列表的时机/顺序
        handlers.add(0, new ResponseViewHandler());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authorizationInterceptor).addPathPatterns("/**");
    }


    @Bean
    public WelusReadyListener welusReadyListener() {
        return new WelusReadyListener();
    }
}