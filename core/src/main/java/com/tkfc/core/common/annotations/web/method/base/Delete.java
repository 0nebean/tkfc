package com.tkfc.core.common.annotations.web.method.base;

import com.tkfc.core.common.annotations.web.info.Author;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * GET请求封装
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/12/23 16:29
 */

@RequestMapping(
        method = {RequestMethod.DELETE}
)
@Author
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Delete {

    @AliasFor(
            annotation = Author.class,
            attribute = "contact"
    )
    String contact() default "";

    @AliasFor(
            annotation = Author.class,
            attribute = "department"
    )
    String department() default "";

    @AliasFor(
            annotation = Author.class,
            attribute = "authors"
    )
    String[] authors();

    String tag();

    @AliasFor(
            annotation = RequestMapping.class,
            attribute = "path"
    )
    String[] path();

    @AliasFor(
            annotation = RequestMapping.class,
            attribute = "params"
    )
    String[] params() default {};

    @AliasFor(
            annotation = RequestMapping.class,
            attribute = "headers"
    )
    String[] headers() default {};

    @AliasFor(
            annotation = RequestMapping.class,
            attribute = "consumes"
    )
    String[] consumes() default {};

    @AliasFor(
            annotation = RequestMapping.class,
            attribute = "produces"
    )
    String[] produces() default {};
}
