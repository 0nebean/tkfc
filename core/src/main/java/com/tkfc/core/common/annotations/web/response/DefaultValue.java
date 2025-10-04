package com.tkfc.core.common.annotations.web.response;

import java.lang.annotation.*;

/**
 * 默认值包装
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/4/13 18:07
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DefaultValue {

    String defaultValue();
    String covertValue();

}
