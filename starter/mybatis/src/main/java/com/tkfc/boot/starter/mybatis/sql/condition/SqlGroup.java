package com.tkfc.boot.starter.mybatis.sql.condition;

import com.tkfc.boot.starter.mybatis.sql.wrapper.interfaces.IWrapper;

import java.io.Serializable;

/**
 * sql 聚合包装
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/10/30 15:50
 */
public interface SqlGroup<Field,Self> extends Serializable {

    Self groupBy(Boolean condition, Field field);

    default Self groupBy(Field field) {
        return groupBy(true, field);
    }
}
