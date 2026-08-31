package com.tkfc.boot.starter.mybatis.sql.build;

import com.tkfc.boot.starter.mybatis.sql.wrapper.SqlWrapper;

public class SqlBuilder {


    /**
     * 获取查询对象
     *
     * @param <T> 查询的类型
     * @return <T> SqlWrapper
     */
    public static <T> SqlWrapper<T> init() {
        return new SqlWrapper<>();
    }
}
