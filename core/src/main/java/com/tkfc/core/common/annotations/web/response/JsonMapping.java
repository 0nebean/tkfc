package com.tkfc.core.common.annotations.web.response;

import java.lang.annotation.*;

/**
 * Json包装类
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/4/13 18:07
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface JsonMapping {
    String mapping() default "";
}
