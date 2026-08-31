package com.tkfc.welus.selector;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * welus selector
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/1/12 11:10
 */
public class WelusImportSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        // BeanDefinition 须在 WelusConfig 之前（构造器注入 Resolver / Interceptor）
        return new String[]{
                "com.tkfc.welus.config.BeanDefinition",
                "com.tkfc.welus.config.ReturnHandlerConfigurer",
                "com.tkfc.welus.config.WelusConfig",
                "com.tkfc.welus.handler.ResponseJsonHandler",
                "com.tkfc.welus.handler.ResponseErrorHandler",
                "com.tkfc.welus.converter.FastJson2HttpMessageConverterConfiguration",
        };
    }
}