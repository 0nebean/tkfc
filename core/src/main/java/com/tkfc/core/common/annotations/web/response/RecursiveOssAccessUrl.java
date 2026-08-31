package com.tkfc.core.common.annotations.web.response;

import java.lang.annotation.*;

/**
 * 递归处理OSS访问URL注解
 * 标记此注解的字段，会递归处理其内部对象的OSS访问URL转换
 *
 * @author 0neBean
 * @version 1.0
 * @since 2025/11/30
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface RecursiveOssAccessUrl {
}

