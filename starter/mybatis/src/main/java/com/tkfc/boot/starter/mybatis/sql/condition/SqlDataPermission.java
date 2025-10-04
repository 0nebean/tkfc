package com.tkfc.boot.starter.mybatis.sql.condition;

import com.tkfc.core.common.pojo.DataPermission;

import java.io.Serializable;

/**
 * sql 数据权限封装
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/3/21 20:11
 */
public interface SqlDataPermission<Self> extends Serializable {


    Self dataPermission(DataPermission dp);

}
