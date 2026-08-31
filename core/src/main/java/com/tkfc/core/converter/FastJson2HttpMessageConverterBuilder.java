package com.tkfc.core.converter;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.filter.ValueFilter;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring.http.converter.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.http.MediaType;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * json 序列化包装
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/12/14 17:05
 */
public class FastJson2HttpMessageConverterBuilder extends HttpMessageConverters {

    /**
     * 初始化并配置一个 FastJsonHttpMessageConverter，包含一组支持的媒体类型和 FastJsonConfig。
     * FastJsonConfig 用于自定义序列化和反序列化行为，例如设置日期格式、字符编码，
     * 以及启用特定的 JSON 读写特性。
     *
     * @return 一个包含已配置的 FastJsonHttpMessageConverter 的 HttpMessageConverters 实例
     */
    public static FastJsonHttpMessageConverter buildFastJson2HttpMessageConverter() {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = getFastJsonConfig();
        converter.setFastJsonConfig(fastJsonConfig);
        List<MediaType> supportedMediaTypes = new ArrayList<>();
        supportedMediaTypes.add(MediaType.APPLICATION_JSON);
        supportedMediaTypes.add(MediaType.APPLICATION_ATOM_XML);
        supportedMediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
        supportedMediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
        supportedMediaTypes.add(MediaType.APPLICATION_PDF);
        supportedMediaTypes.add(MediaType.APPLICATION_RSS_XML);
        supportedMediaTypes.add(MediaType.APPLICATION_XHTML_XML);
        supportedMediaTypes.add(MediaType.APPLICATION_XML);
        supportedMediaTypes.add(MediaType.IMAGE_GIF);
        supportedMediaTypes.add(MediaType.IMAGE_JPEG);
        supportedMediaTypes.add(MediaType.IMAGE_PNG);
        supportedMediaTypes.add(MediaType.TEXT_EVENT_STREAM);
        supportedMediaTypes.add(MediaType.TEXT_HTML);
        supportedMediaTypes.add(MediaType.TEXT_MARKDOWN);
        supportedMediaTypes.add(MediaType.TEXT_PLAIN);
        supportedMediaTypes.add(MediaType.TEXT_XML);
        converter.setSupportedMediaTypes(supportedMediaTypes);
        converter.setDefaultCharset(StandardCharsets.UTF_8);
        return converter;
    }

    /**
     * 自定义值过滤器
     */
    private static class CustomValueFilter implements ValueFilter {
        @Override
        public Object apply(Object object, String name, Object value) {
            if (Objects.isNull(value) && Objects.nonNull(object) && isTimeField(object.getClass(), name)) {
                return "";
            }
            return value;  // 返回其他类型的字段值
        }

        private boolean isTimeField(Class<?> clazz, String fieldName) {
            Field field = findField(clazz, fieldName);
            if (Objects.isNull(field)) {
                return false;
            }
            Class<?> fieldType = field.getType();
            return Temporal.class.isAssignableFrom(fieldType) || Date.class.isAssignableFrom(fieldType);
        }

        private Field findField(Class<?> clazz, String fieldName) {
            Class<?> current = clazz;
            while (Objects.nonNull(current) && !Objects.equals(current, Object.class)) {
                try {
                    return current.getDeclaredField(fieldName);
                } catch (NoSuchFieldException ignored) {
                    current = current.getSuperclass();
                }
            }
            return null;
        }
    }

    /**
     * 获取fastjson2配置
     * @return fastjson2配置
     */
    private static FastJsonConfig getFastJsonConfig() {
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setWriterFilters(new CustomValueFilter());
        //fastjson2 默认格式为yyyy-MM-dd HH:mm 无需重新设置
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        //fastjson2 默认StandardCharsets.UTF_8 无需重新设置
        fastJsonConfig.setCharset(StandardCharsets.UTF_8);
        fastJsonConfig.setReaderFeatures(
                //字段如 vBtn  会被转为 VBtn  处理这样的问题
                JSONReader.Feature.SupportSmartMatch,
                JSONReader.Feature.FieldBased,
                //初始化String字段为空字符串""
                JSONReader.Feature.InitStringFieldAsEmpty,
                //对读取到的字符串值做trim处理
                JSONReader.Feature.TrimString);
        fastJsonConfig.setWriterFeatures(
                //字段如 vBtn  会被转为 VBtn  处理这样的问题
                JSONWriter.Feature.FieldBased,
                //long 转 string 丢失精度问题
                //JSONWriter.Feature.WriteLongAsString,
                // 保留map空的字段
                JSONWriter.Feature.WriteMapNullValue,
                //将List类型的null转成[]
                JSONWriter.Feature.WriteNullListAsEmpty,
                //将String类型的null转成""
                JSONWriter.Feature.WriteNullStringAsEmpty,
                //将Boolean类型的null转成false
                JSONWriter.Feature.WriteNullBooleanAsFalse,
                //日期格式转换
                JSONWriter.Feature.PrettyFormat,
                //将空置输出为缺省值，Number类型的null都输出为0，String类型的null输出为""，数组和Collection类型的输出为[]
                JSONWriter.Feature.NullAsDefaultValue
        );
        return fastJsonConfig;
    }
}
