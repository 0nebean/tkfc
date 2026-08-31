package com.tkfc.core.common.annotations.web.auth;

import com.tkfc.core.constants.StringPool;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.*;

/**
 * 权限注解
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/12/23 16:22
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Inherited
public @interface Authenticated {

    String value() default StringPool.EMPTY;

    boolean needLogin() default false;

}
