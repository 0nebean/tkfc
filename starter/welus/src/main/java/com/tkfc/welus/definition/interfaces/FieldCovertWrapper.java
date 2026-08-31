package com.tkfc.welus.definition.interfaces;

/**
 * 枚举包装
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/1/9 15:24
 */
public interface FieldCovertWrapper {

    /**
     * 包装枚举
     * @param target 目标
     */
    void wrapFieldValue(Object target);

    /**
     *包装json字段
     * @param target 目标
     */
    void warpFieldJsonValue(Object target);

    /**
     *包装oss链接字段
     * @param target 目标
     */
    void warpOssAccessUrl(Object target);
}
