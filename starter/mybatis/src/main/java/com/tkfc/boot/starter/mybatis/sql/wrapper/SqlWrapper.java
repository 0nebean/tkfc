package com.tkfc.boot.starter.mybatis.sql.wrapper;

import com.tkfc.boot.starter.mybatis.sql.enums.SqlKeyword;
import com.tkfc.boot.starter.mybatis.sql.wrapper.abstracts.ExpressionWrapper;
import com.tkfc.boot.starter.mybatis.sql.wrapper.interfaces.IWrapper;
import com.tkfc.core.common.pojo.DataPermission;
import com.tkfc.core.common.pojo.Pagination;
import com.tkfc.core.common.pojo.PhysicallyDeleted;
import com.tkfc.core.common.pojo.Sort;
import com.tkfc.core.constants.StringPool;
import com.tkfc.core.function.SerializableFunction;
import com.tkfc.core.toolkit.CollectionUtil;
import com.tkfc.core.toolkit.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * sql 查询条件封装类
 * 第四层抽象
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/11/11 13:54
 */
public class SqlWrapper<T> extends ExpressionWrapper<SqlWrapper<T>, SerializableFunction<T, ?>> {


    /**
     * sql 片段
     */
    private StringBuilder sqlSegment;
    /**
     * 分页
     */
    private Pagination pagination;
    /**
     * 排序
     */
    private Sort sort;
    /**
     * 物理删除
     */
    private PhysicallyDeleted physicallyDeleted;
    /**
     * 物理删除
     */
    private DataPermission dp;

    private Boolean hasPagination;

    private Boolean withoutIgnoreField;

    private List<String> groupByFields;

    /**
     * 构造中初始化sql条件类
     */
    public SqlWrapper() {
        groupByFields = new ArrayList<>();
        physicallyDeleted = new PhysicallyDeleted();
        sqlSegment = new StringBuilder();
        sort = new Sort();
        pagination = new Pagination();
        hasPagination = false;
        withoutIgnoreField = false;
    }

    @Override
    public String getGroupByFields() {
        return CollectionUtil.listToStringWithComma(groupByFields);
    }

    @Override
    public List<String> getGroupByFieldList() {
        return groupByFields;
    }

    @Override
    public String getSqlSegment() {
        return sqlSegment.toString();
    }

    @Override
    public StringBuilder getSqlBuild() {
        return sqlSegment;
    }

    @Override
    public void setSqlBuild(StringBuilder sqlBuild) {
        this.sqlSegment = sqlBuild;
    }

    public void setSqlSegment(StringBuilder sqlSegment) {
        this.sqlSegment = sqlSegment;
    }

    @Override
    public Pagination getPagination() {
        return pagination;
    }

    @Override
    public void setPagination(Pagination pagination) {
        Integer pageSize = Optional.ofNullable(pagination).map(Pagination::getPageSize).orElse(null);
        if (Objects.nonNull(pageSize)) {
            setHasPagination(true);
        }
        this.pagination = pagination;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public void setSort(Sort sort) {
        if (Objects.nonNull(sort)) {
            sort.setOrderBy(StringUtil.isNotBlank(sort.getOrderBy()) ? StringUtil.camelCaseToUnderline(sort.getOrderBy()) : StringPool.EMPTY);
            this.sort = sort;
        }
    }

    @Override
    public PhysicallyDeleted getPhysicallyDeleted() {
        return physicallyDeleted;
    }

    @Override
    public void setPhysicallyDeleted(PhysicallyDeleted physicallyDeleted) {
        this.physicallyDeleted = physicallyDeleted;
    }

    @Override
    public DataPermission getDp() {
        return dp;
    }

    @Override
    public void setDp(DataPermission dp) {
        this.dp = dp;
    }

    @Override
    public Boolean getHasPagination() {
        return hasPagination;
    }

    @Override
    public void setHasPagination(Boolean hasPagination) {
        this.hasPagination = hasPagination;
    }

    @Override
    public Boolean getWithoutIgnoreField() {
        return withoutIgnoreField;
    }

    @Override
    public void setWithoutIgnoreField() {
        withoutIgnoreField = true;
    }

    @Override
    public IWrapper withDeletedData() {
        physicallyDeleted.setValue(PhysicallyDeleted.DELETED);
        return instance();
    }

    @Override
    protected SqlWrapper<T> instance() {
        return this;
    }

    @Override
    public IWrapper withOutPrefix() {
        String s = getSqlBuild().toString();
        s = s.replaceAll(" t.", SqlKeyword.SPACE.getKeyword());
        setSqlBuild(new StringBuilder(s));
        return instance();
    }


}
