package com.tkfc.core.common.annotations.web.validation;

import java.lang.annotation.*;

/**
 * 空值校验
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/3/1 10:22
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotNullValue {

    String errMsg() default "field of %s valid failure , cause [not null value] ";
}
