package com.tkfc.core.common.annotations.web.action;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.*;

/**
 * 接口定义
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/12/23 16:22
 */
@Controller
@RequestMapping
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Action {

    String tag();

    @AliasFor(
            annotation = RequestMapping.class,
            attribute = "path"
    )
    String[] value() default {};

    @AliasFor(
            annotation = RequestMapping.class,
            attribute = "value"
    )
    String[] path() default {};


}
