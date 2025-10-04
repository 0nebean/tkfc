package com.tkfc.boot.starter.mybatis.sql.condition;

import com.tkfc.core.common.pojo.Sort;

import java.io.Serializable;

/**
 * sql 排序封装
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/10/30 15:38
 */
public interface SqlSort<Field, Self> extends Serializable {

    default Self orderByAsc(Field field) {
        return orderByAsc(true, field);
    }

    default Self orderByDesc(Field field) {
        return orderByDesc(true, field);
    }

    default Self sort(Sort sort) {
        return sort(true, sort);
    }

    Self orderByAsc(Boolean condition, Field field);

    Self orderByDesc(Boolean condition, Field field);

    Self sort(Boolean condition, Sort sort);
}
