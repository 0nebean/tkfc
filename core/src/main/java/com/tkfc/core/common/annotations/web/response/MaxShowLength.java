package com.tkfc.core.common.annotations.web.response;

import java.lang.annotation.*;

/**
 * 最长显示长度
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/4/13 18:07
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MaxShowLength {

    int maxlength() default 20;

}
