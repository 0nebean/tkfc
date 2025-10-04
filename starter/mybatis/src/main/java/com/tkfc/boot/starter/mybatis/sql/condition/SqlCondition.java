package com.tkfc.boot.starter.mybatis.sql.condition;


import com.tkfc.boot.starter.mybatis.sql.enums.SqlKeyword;

import java.io.Serializable;
import java.util.List;

/**
 * sql 条件封装
 *
 * @param <Field> 字段类型
 * @author 0neBean
 */
public interface SqlCondition<Self, Field> extends Serializable {

    Self eq(Boolean condition, Field field, Object value);

    Self neq(Boolean condition, Field field, Object value);

    Self le(Boolean condition, Field field, Object value);

    Self lt(Boolean condition, Field field, Object value);

    Self ge(Boolean condition, Field field, Object value);

    Self gt(Boolean condition, Field field, Object value);

    Self in(Boolean condition, Field field, List<?> value);

    Self notIn(Boolean condition, Field field, List<?> value);

    Self between(Boolean condition, Field field, Object value1, Object value2);

    Self notBetween(Boolean condition, Field field, Object value1, Object value2);

    Self like(Boolean condition, Field field, Object value);

    Self notLike(Boolean condition, Field field, Object value);

    Self likeLeft(Boolean condition, Field field, Object value);

    Self likeRight(Boolean condition, Field field, Object value);

    Self pressDate(Boolean condition, String field, Object value);

    Self pressDate(Boolean condition, String field, Object value1, Object value2);

    Self isNull(Boolean condition, Field field);

    Self isNotNull(Boolean condition, Field field);

    Self withDeleted(Boolean condition);

    Self call(Boolean condition, String applySql, Object... params);

    Self last(Boolean condition, Object value);

    Self withoutIgnoreField();

    default Self withDeleted() {
        return withDeleted(true);
    }

    default Self eq(Field field, Object value) {
        return eq(true, field, value);
    }

    default Self neq(Field field, Object value) {
        return neq(true, field, value);
    }

    default Self le(Field field, Object value) {
        return le(true, field, value);
    }

    default Self lt(Field field, Object value) {
        return lt(true, field, value);
    }

    default Self ge(Field field, Object value) {
        return ge(true, field, value);
    }

    default Self gt(Field field, Object value) {
        return gt(true, field, value);
    }

    default Self in(Field field, List<?> value) {
        return in(true, field, value);
    }

    default Self notIn(Field field, List<?> value) {
        return notIn(true, field, value);
    }

    default Self between(Field field, Object value1, Object value2) {
        return between(true, field, value1, value2);
    }

    default Self notBetween(Field field, Object value1, Object value2) {
        return notBetween(true, field, value1, value2);
    }

    default Self like(Field field, Object value) {
        return like(true, field, value);
    }

    default Self notLike(Field field, Object value) {
        return notLike(true, field, value);
    }

    default Self likeLeft(Field field, Object value) {
        return likeLeft(true, field, value);
    }

    default Self likeRight(Field field, Object value) {
        return likeRight(true, field, value);
    }

    default Self pressDate( String field, Object value) {
        return pressDate(true, field, value);
    }
    default Self pressDate(String field, Object value1, Object value2) {
        return pressDate(true, field,value1,value2);
    }

    default Self isNull(Field field) {
        return isNull(true, field);
    }

    default Self isNotNull(Field field) {
        return isNotNull(true, field);
    }

    default Self call(String applySql, Object... params) {
        return call(true, applySql, params);
    }

    default Self last(Object value) {
        return last(true, value);
    }

}
