package com.tkfc.boot.starter.mybatis.sql.condition;

import com.tkfc.core.common.pojo.Pagination;

import java.io.Serializable;

/**
 * sql 分页封装
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/10/30 15:22
 */
public interface SqlPagination<Self> extends Serializable {

    Self queryAll();

    Self page(Pagination pagination);

    Self page(Integer pageNumber);

    Self page(Integer pageNumber, Integer pageSize);

}
