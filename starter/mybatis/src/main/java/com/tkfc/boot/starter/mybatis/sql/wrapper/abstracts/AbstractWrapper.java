package com.tkfc.boot.starter.mybatis.sql.wrapper.abstracts;

import com.tkfc.boot.starter.mybatis.sql.condition.*;
import com.tkfc.boot.starter.mybatis.sql.enums.SqlKeyword;
import com.tkfc.boot.starter.mybatis.toolkit.LambdaColumnUtil;
import com.tkfc.core.common.pojo.DataPermission;
import com.tkfc.core.common.pojo.Pagination;
import com.tkfc.core.common.pojo.PhysicallyDeleted;
import com.tkfc.core.common.pojo.Sort;
import com.tkfc.core.constants.StringPool;
import com.tkfc.core.function.SerializableConsumer;
import com.tkfc.core.function.SerializableFunction;
import com.tkfc.core.toolkit.DateUtil;
import com.tkfc.core.toolkit.StringUtil;

import java.util.List;

/**
 * sql 包装类 抽象类
 * 第三层抽象
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/11/2 14:22
 */
public abstract class AbstractWrapper<Self, Field> extends BaseWrapper<Self> implements SqlCondition<Self, Field>, SqlGroup<Field, Self>, SqlSort<Field, Self>, SqlSplice<Self>, SqlPagination<Self>, SqlDataPermission<Self> {

    protected Self eq(Boolean condition, String field, Object value) {
        if (condition) {
            smartSqlConnector();
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(covertFieldPrefix(field));
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(SqlKeyword.EQ.getKeyword());
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(valueToSqlString(value));
        }
        return instance();
    }

    protected Self neq(Boolean condition, String field, Object value) {
        if (condition) {
            smartSqlConnector();
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(covertFieldPrefix(field));
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(SqlKeyword.NE.getKeyword());
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(valueToSqlString(value));
        }
        return instance();
    }

    protected Self le(Boolean condition, String field, Object value) {
        if (condition) {
            smartSqlConnector();
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(covertFieldPrefix(field));
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(SqlKeyword.LE.getKeyword());
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(valueToSqlString(value));
        }
        return instance();
    }

    protected Self lt(Boolean condition, String field, Object value) {
        if (condition) {
            smartSqlConnector();
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(covertFieldPrefix(field));
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(SqlKeyword.LT.getKeyword());
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(valueToSqlString(value));
        }
        return instance();
    }

    protected Self ge(Boolean condition, String field, Object value) {
        if (condition) {
            smartSqlConnector();
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(covertFieldPrefix(field));
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(SqlKeyword.GE.getKeyword());
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(valueToSqlString(value));
        }
        return instance();
    }

    protected Self gt(Boolean condition, String field, Object value) {
        if (condition) {
            smartSqlConnector();
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(covertFieldPrefix(field));
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(SqlKeyword.GT.getKeyword());
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(valueToSqlString(value));
        }
        return instance();
    }

    protected Self in(Boolean condition, String field, List<?> value) {
        if (condition) {
            smartSqlConnector();
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(covertFieldPrefix(field));
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(SqlKeyword.IN.getKeyword());
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(SqlKeyword.LEFT_BRACKET.getKeyword());
            getSqlBuild().append(inCollectionToSqlEnumerable(value));
            getSqlBuild().append(SqlKeyword.RIGHT_BRACKET.getKeyword());
        }
        return instance();
    }

    protected Self notIn(Boolean condition, String field, List<?> value) {
        if (condition) {
            smartSqlConnector();
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(covertFieldPrefix(field));
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(SqlKeyword.NOT_IN.getKeyword());
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(SqlKeyword.LEFT_BRACKET.getKeyword());
            getSqlBuild().append(inCollectionToSqlEnumerable(value));
            getSqlBuild().append(SqlKeyword.RIGHT_BRACKET.getKeyword());
        }
        return instance();
    }

    protected Self between(Boolean condition, String field, Object value1, Object value2) {
        if (condition) {
            smartSqlConnector();
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(covertFieldPrefix(field));
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(SqlKeyword.BETWEEN.getKeyword());
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(valueToSqlString(value1));
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(SqlKeyword.AND.getKeyword());
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(valueToSqlString(value2));
        }
        return instance();
    }

    protected Self notBetween(Boolean condition, String field, Object value1, Object value2) {
        if (condition) {
            smartSqlConnector();
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(covertFieldPrefix(field));
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(SqlKeyword.NOT_BETWEEN.getKeyword());
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(valueToSqlString(value1));
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(SqlKeyword.AND.getKeyword());
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(valueToSqlString(value2));
        }
        return instance();
    }

    protected Self like(Boolean condition, String field, Object value) {
        if (condition) {
            smartSqlConnector();
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(covertFieldPrefix(field));
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(SqlKeyword.LIKE.getKeyword());
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(SqlKeyword.SINGLE_QUOTE.getKeyword());
            getSqlBuild().append(SqlKeyword.PERCENT.getKeyword());
            getSqlBuild().append(value);
            getSqlBuild().append(SqlKeyword.PERCENT.getKeyword());
            getSqlBuild().append(SqlKeyword.SINGLE_QUOTE.getKeyword());
        }
        return instance();
    }

    protected Self notLike(Boolean condition, String field, Object value) {
        if (condition) {
            smartSqlConnector();
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(covertFieldPrefix(field));
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(SqlKeyword.NOT_LIKE.getKeyword());
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(SqlKeyword.SINGLE_QUOTE.getKeyword());
            getSqlBuild().append(SqlKeyword.PERCENT.getKeyword());
            getSqlBuild().append(value);
            getSqlBuild().append(SqlKeyword.PERCENT.getKeyword());
            getSqlBuild().append(SqlKeyword.SINGLE_QUOTE.getKeyword());
        }
        return instance();
    }

    protected Self likeLeft(Boolean condition, String field, Object value) {
        if (condition) {
            smartSqlConnector();
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(covertFieldPrefix(field));
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(SqlKeyword.LIKE.getKeyword());
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(SqlKeyword.SINGLE_QUOTE.getKeyword());
            getSqlBuild().append(SqlKeyword.PERCENT.getKeyword());
            getSqlBuild().append(value);
            getSqlBuild().append(SqlKeyword.SINGLE_QUOTE.getKeyword());
        }
        return instance();
    }

    protected Self likeRight(Boolean condition, String field, Object value) {
        if (condition) {
            smartSqlConnector();
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(covertFieldPrefix(field));
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(SqlKeyword.LIKE.getKeyword());
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(SqlKeyword.SINGLE_QUOTE.getKeyword());
            getSqlBuild().append(value);
            getSqlBuild().append(SqlKeyword.PERCENT.getKeyword());
            getSqlBuild().append(SqlKeyword.SINGLE_QUOTE.getKeyword());
        }
        return instance();
    }

    protected Self isNull(Boolean condition, String field) {
        if (condition) {
            smartSqlConnector();
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(covertFieldPrefix(field));
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(SqlKeyword.IS_NULL.getKeyword());
        }
        return instance();
    }

    protected Self isNotNull(Boolean condition, String field) {
        if (condition) {
            smartSqlConnector();
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(covertFieldPrefix(field));
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(SqlKeyword.IS_NOT_NULL.getKeyword());
        }
        return instance();
    }

    @Override
    public Self eq(Boolean condition, Field field, Object value) {
        String name = LambdaColumnUtil.getName((SerializableFunction<?, ?>) field);
        return eq(condition, name, value);
    }

    @Override
    public Self neq(Boolean condition, Field field, Object value) {
        String name = LambdaColumnUtil.getName((SerializableFunction<?, ?>) field);
        return neq(condition, name, value);
    }

    @Override
    public Self le(Boolean condition, Field field, Object value) {
        String name = LambdaColumnUtil.getName((SerializableFunction<?, ?>) field);
        return le(condition, name, value);
    }

    @Override
    public Self lt(Boolean condition, Field field, Object value) {
        String name = LambdaColumnUtil.getName((SerializableFunction<?, ?>) field);
        return lt(condition, name, value);
    }

    @Override
    public Self ge(Boolean condition, Field field, Object value) {
        String name = LambdaColumnUtil.getName((SerializableFunction<?, ?>) field);
        return ge(condition, name, value);
    }

    @Override
    public Self gt(Boolean condition, Field field, Object value) {
        String name = LambdaColumnUtil.getName((SerializableFunction<?, ?>) field);
        return gt(condition, name, value);
    }

    @Override
    public Self in(Boolean condition, Field field, List<?> value) {
        String name = LambdaColumnUtil.getName((SerializableFunction<?, ?>) field);
        return in(condition, name, value);
    }

    @Override
    public Self notIn(Boolean condition, Field field, List<?> value) {
        String name = LambdaColumnUtil.getName((SerializableFunction<?, ?>) field);
        return notIn(condition, name, value);
    }

    @Override
    public Self between(Boolean condition, Field field, Object value1, Object value2) {
        String name = LambdaColumnUtil.getName((SerializableFunction<?, ?>) field);
        return between(condition, name, value1, value2);
    }

    @Override
    public Self notBetween(Boolean condition, Field field, Object value1, Object value2) {
        String name = LambdaColumnUtil.getName((SerializableFunction<?, ?>) field);
        return notBetween(condition, name, value1, value2);
    }

    @Override
    public Self like(Boolean condition, Field field, Object value) {
        String name = LambdaColumnUtil.getName((SerializableFunction<?, ?>) field);
        return like(condition, name, value);
    }

    @Override
    public Self notLike(Boolean condition, Field field, Object value) {
        String name = LambdaColumnUtil.getName((SerializableFunction<?, ?>) field);
        return notLike(condition, name, value);
    }

    @Override
    public Self likeLeft(Boolean condition, Field field, Object value) {
        String name = LambdaColumnUtil.getName((SerializableFunction<?, ?>) field);
        return likeLeft(condition, name, value);
    }

    @Override
    public Self likeRight(Boolean condition, Field field, Object value) {
        String name = LambdaColumnUtil.getName((SerializableFunction<?, ?>) field);
        return likeRight(condition, name, value);
    }

    @Override
    public Self isNull(Boolean condition, Field field) {
        String name = LambdaColumnUtil.getName((SerializableFunction<?, ?>) field);
        return isNull(condition, name);
    }

    @Override
    public Self isNotNull(Boolean condition, Field field) {
        String name = LambdaColumnUtil.getName((SerializableFunction<?, ?>) field);
        return isNotNull(condition, name);
    }

    @Override
    public Self call(Boolean condition, String applySql, Object... params) {
        if (condition) {
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(applySql);
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(SqlKeyword.LEFT_BRACKET.getKeyword());
            for (Object param : params) {
                getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
                getSqlBuild().append(valueToSqlString(param));
                getSqlBuild().append(SqlKeyword.COMMA.getKeyword());
            }
            getSqlBuild().deleteCharAt(getSqlBuild().length() - 1);
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(SqlKeyword.RIGHT_BRACKET.getKeyword());
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
        }
        return instance();
    }


    @Override
    public Self pressDate(Boolean condition, String field, Object value) {
        if (condition) {
            smartSqlConnector();
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(covertFieldPrefix(field));
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(SqlKeyword.EQ.getKeyword());
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(DateUtil.getPressDateByDateStr(value.toString()));
        }
        return instance();
    }

    @Override
    public Self pressDate(Boolean condition, String field, Object value1, Object value2) {
        if (condition) {
            smartSqlConnector();
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(covertFieldPrefix(field));
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(SqlKeyword.BETWEEN.getKeyword());
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(DateUtil.getPressDateByDateStr(value1.toString()));
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(SqlKeyword.AND.getKeyword());
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(DateUtil.getPressDateByDateStr(value2.toString()));
        }
        return instance();
    }

    @Override
    public Self last(Boolean condition, Object value) {
        if (condition) {
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(value);
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
        }
        return instance();
    }

    @Override
    public Self withoutIgnoreField() {
        setWithoutIgnoreField();
        return instance();
    }

    @Override
    public Self withDeleted(Boolean condition) {
        getPhysicallyDeleted().setValue(PhysicallyDeleted.DELETED);
        return instance();
    }

    @Override
    public Self groupBy(Boolean condition, Field field) {
        if (condition) {
            String name = LambdaColumnUtil.getName((SerializableFunction<?, ?>) field);
            getGroupByFieldList().add(name);
        }
        return instance();
    }

    @Override
    public Self and(Boolean condition, SerializableConsumer<Self> consumer) {
        getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
        getSqlBuild().append(SqlKeyword.AND.getKeyword());
        consumer.accept(instance());
        return instance();
    }


    @Override
    public Self andBody(Boolean condition, SerializableConsumer<Self> consumer) {
        getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
        if (StringUtil.isNotBlank(getSqlBuild().toString().trim())) {
            getSqlBuild().append(SqlKeyword.AND.getKeyword());
        }
        getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
        getSqlBuild().append(SqlKeyword.LEFT_BRACKET.getKeyword());
        consumer.accept(instance());
        getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
        getSqlBuild().append(SqlKeyword.RIGHT_BRACKET.getKeyword());
        return instance();
    }

    @Override
    public Self or(Boolean condition, SerializableConsumer<Self> consumer) {
        getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
        getSqlBuild().append(SqlKeyword.OR.getKeyword());
        consumer.accept(instance());
        return instance();
    }

    @Override
    public Self orBody(Boolean condition, SerializableConsumer<Self> consumer) {
        getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
        if (StringUtil.isNotBlank(getSqlBuild().toString().trim())) {
            getSqlBuild().append(SqlKeyword.OR.getKeyword());
        }
        getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
        getSqlBuild().append(SqlKeyword.LEFT_BRACKET.getKeyword());
        consumer.accept(instance());
        getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
        getSqlBuild().append(SqlKeyword.RIGHT_BRACKET.getKeyword());
        return instance();
    }

    @Override
    public Self condition(Boolean condition, SerializableConsumer<Self> consumer) {
        if (condition) {
            consumer.accept(instance());
        }
        return instance();
    }

    @Override
    public Self exists(Boolean condition, SerializableConsumer<Self> consumer) {
        getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
        getSqlBuild().append(SqlKeyword.EXISTS.getKeyword());
        getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
        getSqlBuild().append(SqlKeyword.LEFT_BRACKET.getKeyword());
        consumer.accept(instance());
        getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
        getSqlBuild().append(SqlKeyword.RIGHT_BRACKET.getKeyword());
        return instance();
    }

    @Override
    public Self notExists(Boolean condition, SerializableConsumer<Self> consumer) {
        getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
        getSqlBuild().append(SqlKeyword.NOT_EXISTS.getKeyword());
        getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
        getSqlBuild().append(SqlKeyword.LEFT_BRACKET.getKeyword());
        consumer.accept(instance());
        getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
        getSqlBuild().append(SqlKeyword.RIGHT_BRACKET.getKeyword());
        return instance();
    }

    @Override
    public Self having(Boolean condition, SerializableConsumer<Self> consumer) {
        getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
        getSqlBuild().append(SqlKeyword.HAVING.getKeyword());
        consumer.accept(instance());
        return instance();
    }

    @Override
    public Self queryAll() {
        setPagination(null);
        return instance();
    }

    @Override
    public Self page(Pagination pagination) {
        setHasPagination(true);
        setPagination(pagination);
        return instance();
    }

    @Override
    public Self page(Integer pageNumber) {
        setHasPagination(true);
        getPagination().setCurrentPage(pageNumber);
        return instance();
    }

    @Override
    public Self page(Integer pageNumber, Integer pageSize) {
        setHasPagination(true);
        getPagination().setCurrentPage(pageNumber);
        getPagination().setPageSize(pageSize);
        return instance();
    }

    @Override
    public Self orderByAsc(Boolean condition, Field field) {
        if (condition) {
            String name = LambdaColumnUtil.getName((SerializableFunction<?, ?>) field);
            getSort().setOrderBy(name);
            getSort().setSort(SqlKeyword.ASC.getKeyword());
        }
        return instance();
    }

    @Override
    public Self orderByDesc(Boolean condition, Field field) {
        if (condition) {
            String name = LambdaColumnUtil.getName((SerializableFunction<?, ?>) field);
            getSort().setOrderBy(name);
            getSort().setSort(SqlKeyword.DESC.getKeyword());
        }
        return instance();
    }

    @Override
    public Self sort(Boolean condition, Sort sort) {
        if (condition) {
            getSort().setOrderBy(sort.getOrderBy());
            getSort().setSort(sort.getSort());
        }
        return instance();
    }

    @Override
    public Self dataPermission(DataPermission dp) {
        setDp(dp);
        return instance();
    }

    private void smartSqlConnector() {
        String s = getSqlBuild().toString().trim();
        if (StringUtil.isNotBlank(s) && !s.endsWith(SqlKeyword.AND.getKeyword()) && !s.endsWith(SqlKeyword.OR.getKeyword()) && !s.endsWith(StringPool.LEFT_BRACKET)) {
            getSqlBuild().append(SqlKeyword.SPACE.getKeyword());
            getSqlBuild().append(SqlKeyword.AND.getKeyword());
        }
    }

    private static String covertFieldPrefix(String field) {
        return StringUtil.concat("t", StringPool.DOT, field);
    }

}
