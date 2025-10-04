package com.tkfc.boot.starter.mybatis.sql.wrapper.interfaces;

import com.tkfc.core.common.pojo.DataPermission;
import com.tkfc.core.common.pojo.Pagination;
import com.tkfc.core.common.pojo.PhysicallyDeleted;
import com.tkfc.core.common.pojo.Sort;

import java.io.Serializable;
import java.util.List;

/**
 * sql 包装类 顶级接口
 * 第一层接口
 *
 * @author 0neBean
 */
public interface IWrapper extends Serializable {

    /**
     * 用于传递
     */
    default void accept() {

    }

    /**
     * 设置SQL 缓存
     */
    void setSqlBuild(StringBuilder sqlBuild);

    /**
     * 获取SQL 缓存
     */
    StringBuilder getSqlBuild();

    /**
     * 获取SQL 片段
     */
    String getSqlSegment();

    /**
     * 获取group字段
     */
    String getGroupByFields();

    /**
     * 获取group字段
     */
    List<String> getGroupByFieldList();

    /**
     * 分页
     */
    Pagination getPagination();

    void setPagination(Pagination pagination);

    /**
     * 物理删除
     */
    PhysicallyDeleted getPhysicallyDeleted();

    void setPhysicallyDeleted(PhysicallyDeleted physicallyDeleted);

    /**
     * 排序
     */
    Sort getSort();

    void setSort(Sort sort);

    /**
     * 数据权限
     */
    DataPermission getDp();

    void setDp(DataPermission dp);

    /**
     * 是否分页
     */
    Boolean getHasPagination();

    void setHasPagination(Boolean hasPagination);

    IWrapper withOutPrefix();

    /**
     * 查询过滤忽略的字段
     */
    Boolean getWithoutIgnoreField();

    void setWithoutIgnoreField();

    /**
     * 查询过滤忽略的字段
     */
    IWrapper withDeletedData();
}
