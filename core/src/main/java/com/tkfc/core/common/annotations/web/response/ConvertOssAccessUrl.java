package com.tkfc.core.common.annotations.web.response;

import java.lang.annotation.*;


/**
 * 包装oss链接
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/12/14 17:05
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Inherited
public @interface ConvertOssAccessUrl {
}
