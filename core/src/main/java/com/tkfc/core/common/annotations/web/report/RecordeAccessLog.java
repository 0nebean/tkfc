package com.tkfc.core.common.annotations.web.report;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 记录访问日志注解
 *
 * @author 0neBean
 * @version 1.0
 * @since 2024-12-17 18:06:32
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RecordeAccessLog {
}
