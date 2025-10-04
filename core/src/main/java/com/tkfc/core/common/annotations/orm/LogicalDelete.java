package com.tkfc.core.common.annotations.orm;

import java.lang.annotation.*;

/**
 * 逻辑删除
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/11/11 17:27
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
@Inherited
public @interface LogicalDelete {

}
