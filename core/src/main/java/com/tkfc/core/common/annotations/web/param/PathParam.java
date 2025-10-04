package com.tkfc.core.common.annotations.web.param;

import java.lang.annotation.*;

/**
 * path 参数
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/12/24 17:03
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PathParam {

    String tag();

    String example() default "";

}
