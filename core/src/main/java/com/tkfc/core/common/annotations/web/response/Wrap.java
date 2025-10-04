package com.tkfc.core.common.annotations.web.response;

import java.lang.annotation.*;

/**
 * 包装接口返回
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/12/23 16:22
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
@Inherited
public @interface Wrap {

}
