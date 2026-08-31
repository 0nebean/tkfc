package com.tkfc.welus.converter;

import com.alibaba.fastjson2.support.spring.http.converter.FastJsonHttpMessageConverter;
import com.tkfc.core.converter.FastJson2HttpMessageConverterBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * json 序列化包装
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/12/14 17:05
 */
@Configuration
@ConditionalOnClass({FastJsonHttpMessageConverter.class})
@ConditionalOnProperty(name = {"spring.http.converters.preferred-json-mapper"}, havingValue = "fastjson", matchIfMissing = true)
public class FastJson2HttpMessageConverterConfiguration {

    protected FastJson2HttpMessageConverterConfiguration() {
    }

    @Bean("fastJsonHttpMessageConverter")
    @ConditionalOnMissingBean({FastJsonHttpMessageConverter.class})
    public HttpMessageConverters fastJsonHttpMessageConverter() {
        return new HttpMessageConverters(FastJson2HttpMessageConverterBuilder.buildFastJson2HttpMessageConverter());
    }

}
