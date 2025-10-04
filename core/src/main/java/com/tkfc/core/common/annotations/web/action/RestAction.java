package com.tkfc.core.common.annotations.web.action;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

/**
 * restful 接口定义
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/12/23 16:22
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RestController
@RequestMapping
public @interface RestAction {

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
