package com.tkfc.core.common.annotations.web.param;

import java.lang.annotation.*;

/**
 * url 参数
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/12/24 17:03
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UrlParam {

    String tag();

    String defaultValue() default "";

    String example() default "";

    boolean required() default true;

}
