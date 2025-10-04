package com.tkfc.welus.definition.interfaces;

import org.springframework.core.MethodParameter;

import java.lang.reflect.Field;

/**
 * 字段校验
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/3/1 10:59
 */
public interface FieldValidChecker {

    /**
     * 获取方法参数的上的校验注解
     *
     * @param methodParameter 方法参数
     * @param key             方法参
     * @param value           方法参数值
     */
    void checkValidAnnotations(MethodParameter methodParameter, String key, Object value);

    /**
     * 获取方法参数的上的校验注解
     *
     * @param field 参数
     * @param key   方法参
     * @param value 方法参数值
     */
    void checkValidAnnotations(Field field, String key, Object value);

    /**
     * 基础类型的转换
     *
     * @param clazz class
     * @param value 值
     * @return 结果
     */
    Object basicTypeCast(Class<?> clazz, Object value);


}
