package com.tkfc.core.common.annotations.orm;

import java.lang.annotation.*;

/**
 * 重复数据更新
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/08/04 20:27
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface OnDuplicateKeyUpdate {
}
