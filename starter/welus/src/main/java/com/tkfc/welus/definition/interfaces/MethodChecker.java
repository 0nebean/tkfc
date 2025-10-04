package com.tkfc.welus.definition.interfaces;

import org.springframework.core.MethodParameter;

/**
 * 检查请求方法是否和注解匹配
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/1/7 17:45
 */
public interface MethodChecker {


    /**
     * 获取请求注解path
     *
     * @param methodParameter 方法句柄
     * @return path
     */
    String[] getMethodMappings(MethodParameter methodParameter);

    /**
     * 是否是页面请求
     *
     * @param methodParameter 方法句柄
     * @return bool
     */
    Boolean isViewAccess(MethodParameter methodParameter);

    /**
     * 是否是JSON请求
     *
     * @param methodParameter 方法句柄
     * @return bool
     */
    Boolean isJsonAccess(MethodParameter methodParameter);

}
