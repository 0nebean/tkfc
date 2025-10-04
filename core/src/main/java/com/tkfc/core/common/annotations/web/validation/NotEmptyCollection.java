package com.tkfc.core.common.annotations.web.validation;

import java.lang.annotation.*;

/**
 * 集合内容不为空校验
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/3/1 10:22
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotEmptyCollection {

    String errMsg() default "field of %s valid failure , cause [not empty collection] ";
}
