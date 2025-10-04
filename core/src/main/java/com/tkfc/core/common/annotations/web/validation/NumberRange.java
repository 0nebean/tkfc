package com.tkfc.core.common.annotations.web.validation;

import java.lang.annotation.*;

/**
 * 数字区间校验
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/3/1 10:22
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NumberRange {


    String errMsg() default "field of %s valid failure , cause [number range, max = %f, min = %f] ";

    long min() default 0L;

    long max() default 9223372036854775807L;
}
