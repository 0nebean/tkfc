package com.tkfc.welus.config;

import com.tkfc.welus.handler.ResponseViewHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 返回拦截器配置
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/1/9 17:37
 */
@Configuration
@SuppressWarnings("all")
public class ReturnHandlerConfigurer {

    @Lazy
    @Resource
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    /**
     * 解决ReturnHandler不生效问题
     */
    @PostConstruct
    public void init() {
        final List<HandlerMethodReturnValueHandler> originalHandlers =
                new ArrayList<>(Objects.requireNonNull(requestMappingHandlerAdapter.getReturnValueHandlers()));
        final int deferredPos = obtainValueHandlerPosition(originalHandlers);
        originalHandlers.add(deferredPos - 1, new ResponseViewHandler());
        requestMappingHandlerAdapter.setReturnValueHandlers(originalHandlers);
    }

    private int obtainValueHandlerPosition(final List<HandlerMethodReturnValueHandler> originalHandlers) {
        for (int i = 0; i < originalHandlers.size(); i++) {
            final HandlerMethodReturnValueHandler valueHandler = originalHandlers.get(i);
            if (HttpEntityMethodProcessor.class.isAssignableFrom(valueHandler.getClass())) {
                return i;
            }
        }
        return -1;
    }
}
