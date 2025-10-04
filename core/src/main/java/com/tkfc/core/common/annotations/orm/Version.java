package com.tkfc.core.common.annotations.orm;

import java.lang.annotation.*;

/**
 * 版本信息
 * @author zetsu
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
@Inherited
public @interface Version {
}
