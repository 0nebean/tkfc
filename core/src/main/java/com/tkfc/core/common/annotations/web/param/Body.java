package com.tkfc.core.common.annotations.web.param;

import java.lang.annotation.*;

/**
 * 参数体对象
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/12/24 17:03
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Body {

    String tag();
}
