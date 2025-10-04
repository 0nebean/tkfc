package com.tkfc.boot.starter.mus.annotations;

import org.springframework.stereotype.Service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * actor 实例注解
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/12/23 16:29
 */

@Service
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Actor {

    String topic();
    String group() default "";

}
