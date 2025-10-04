package com.tkfc.core.common.annotations.orm;

import java.lang.annotation.*;

/**
 * 排序字段
 * @author 0neBean
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface OrderBy {
    String value();
}
