package com.tkfc.core.common.annotations.web.validation;

import java.lang.annotation.*;

/**
 * 集合size校验
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/3/1 10:22
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CollectionSize {

    String errMsg() default "field of %s valid failure , cause [collection size, max = %d, min = %d] ";

    int min() default 0;

    int max() default 2147483647;

}
