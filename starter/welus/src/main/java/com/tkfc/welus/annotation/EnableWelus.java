package com.tkfc.welus.annotation;

import com.tkfc.welus.selector.WelusImportSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开启welus
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/1/12 9:51
 */
@Import({WelusImportSelector.class})
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableWelus {

    String[] actionPackages();


}
