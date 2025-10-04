package com.tkfc.core.common.annotations.orm;

import java.lang.annotation.*;

/**
 * 表名
 * @author 0neBean
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface TableName {
    String value();
}
