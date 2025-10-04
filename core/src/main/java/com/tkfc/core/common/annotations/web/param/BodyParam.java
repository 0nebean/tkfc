package com.tkfc.core.common.annotations.web.param;

import java.lang.annotation.*;

/**
 * request body 参数
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/12/24 17:03
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BodyParam {

    String tag();

}
