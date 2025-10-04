package com.tkfc.core.enums.base;

/**
 * 顶级枚举
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/4/14 19:01
 */
public interface BaseEnums<T> {

    /**
     * 获取注释
     *
     * @return 注释
     */
    String getDescription();

    /**
     * 获取值
     *
     * @return 值
     */
    T getValue();

    /**
     * 获取排序值
     *
     * @return 排序值
     */
    Integer getSort();

    /**
     * 获取所有枚举值
     *
     * @return 所有枚举值
     */
    BaseEnums<T>[] getValues();

}
