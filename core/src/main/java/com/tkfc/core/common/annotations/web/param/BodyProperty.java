package com.tkfc.core.common.annotations.web.param;


import java.lang.annotation.*;

/**
 * 参数属性
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/12/24 17:03
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BodyProperty {

    String tag();

    String example() default "";

    String excelColumn() default "";

}
