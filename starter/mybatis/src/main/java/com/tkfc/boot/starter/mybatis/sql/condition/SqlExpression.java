package com.tkfc.boot.starter.mybatis.sql.condition;

import com.tkfc.core.common.pojo.BasePageExpressionRequest;
import com.tkfc.core.common.pojo.Pagination;
import com.tkfc.core.common.pojo.PhysicallyDeleted;
import com.tkfc.core.common.pojo.Sort;

import java.io.Serializable;

/**
 * sql表达式封装
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/2/22 18:22
 */
public interface SqlExpression<Self> extends Serializable {

    Self expression(BasePageExpressionRequest basePageRequest);

    Self expression(String[] expressions);

    Self expression(String[] expressions, Sort sort);

    Self expression(String[] expressions, Pagination pagination);

    Self expression(String[] expressions, Pagination pagination, Sort sort);

    Self expression(String[] expressions, PhysicallyDeleted physicallyDeleted);

    Self expression(String[] expressions, Sort sort, PhysicallyDeleted physicallyDeleted);

    Self expression(String[] expressions, Pagination pagination, PhysicallyDeleted physicallyDeleted);

    Self expression(String[] expressions, Pagination pagination, Sort sort, PhysicallyDeleted physicallyDeleted);

    Self expression(BasePageExpressionRequest basePageRequest, String... AllowFields);

    Self expression(String[] expressions, String... AllowFields);

    Self expression(String[] expressions, Sort sort, String... AllowFields);

    Self expression(String[] expressions, Pagination pagination, String... AllowFields);

    Self expression(String[] expressions, Pagination pagination, Sort sort, String... AllowFields);

    Self expression(String[] expressions, PhysicallyDeleted physicallyDeleted, String... AllowFields);

    Self expression(String[] expressions, Sort sort, PhysicallyDeleted physicallyDeleted, String... AllowFields);

    Self expression(String[] expressions, Pagination pagination, PhysicallyDeleted physicallyDeleted, String... AllowFields);

    Self expression(String[] expressions, Pagination pagination, Sort sort, PhysicallyDeleted physicallyDeleted, String... AllowFields);


}
