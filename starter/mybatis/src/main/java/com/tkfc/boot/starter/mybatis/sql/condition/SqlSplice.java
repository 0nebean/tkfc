package com.tkfc.boot.starter.mybatis.sql.condition;

import com.tkfc.core.function.SerializableConsumer;

import java.io.Serializable;

/**
 * 分割SQl关键字
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/10/30 21:22
 */
public interface SqlSplice<Self> extends Serializable {


    Self and(Boolean condition, SerializableConsumer<Self> consumer);

    Self andBody(Boolean condition, SerializableConsumer<Self> consumer);

    Self or(Boolean condition, SerializableConsumer<Self> consumer);

    Self orBody(Boolean condition, SerializableConsumer<Self> consumer);

    Self condition(Boolean condition, SerializableConsumer<Self> consumer);

    Self exists(Boolean condition, SerializableConsumer<Self> consumer);

    Self notExists(Boolean condition, SerializableConsumer<Self> consumer);

    Self having(Boolean condition, SerializableConsumer<Self> consumer);

    default Self condition(SerializableConsumer<Self> consumer) {
        return condition(true, consumer);

    }

    default Self exists(SerializableConsumer<Self> consumer) {
        return exists(true, consumer);
    }

    default Self notExists(SerializableConsumer<Self> consumer) {
        return notExists(true, consumer);
    }

    default Self having(SerializableConsumer<Self> consumer) {
        return having(true, consumer);
    }

    default Self or(SerializableConsumer<Self> consumer) {
        return or(true, consumer);
    }

    default Self orBody(SerializableConsumer<Self> consumer) {
        return orBody(true, consumer);
    }

    default Self and(SerializableConsumer<Self> consumer) {
        return and(true, consumer);
    }

    default Self andBody(SerializableConsumer<Self> consumer) {
        return andBody(true, consumer);
    }

}
