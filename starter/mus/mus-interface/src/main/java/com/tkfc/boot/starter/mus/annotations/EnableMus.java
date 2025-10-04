package com.tkfc.boot.starter.mus.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开启 actor
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/1/12 9:51
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableMus {

    String[] actorPackages();

}
