package com.tkfc.core.common.annotations.elasticsearch;

import com.tkfc.core.enums.elasticsearch.ESFieldType;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author 0neBean
 * @since  2024/1/18
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@Inherited
public @interface ESField {

    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";

    ESFieldType type();

    String analyzer() default "";
}
