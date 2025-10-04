package com.tkfc.core.common.annotations.web.info;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 作者信息
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/12/23 16:36
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Author {
    String[] authors() default {};

    String department() default "";

    String contact() default "";
}
