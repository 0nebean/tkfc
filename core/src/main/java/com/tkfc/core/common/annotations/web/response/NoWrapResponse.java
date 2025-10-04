package com.tkfc.core.common.annotations.web.response;

import java.lang.annotation.*;


/**
 * 不包装通用返回结果过
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/12/14 17:05
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Inherited
public @interface NoWrapResponse {
    String[] key() default "data";
}
