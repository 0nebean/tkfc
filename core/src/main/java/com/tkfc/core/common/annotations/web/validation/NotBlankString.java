package com.tkfc.core.common.annotations.web.validation;

import java.lang.annotation.*;

/**
 * 字符串不为空白校验
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/3/1 10:24
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotBlankString {

    String errMsg() default "field of %s valid failure , cause [not blank string] ";

}
