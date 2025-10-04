package com.tkfc.boot.starter.mybatis.sql.wrapper.abstracts;

import com.tkfc.boot.starter.mybatis.sql.condition.SqlExpression;
import com.tkfc.boot.starter.mybatis.sql.enums.SqlOperatorType;
import com.tkfc.boot.starter.mybatis.sql.enums.SqlSpliceType;
import com.tkfc.core.common.pojo.BasePageExpressionRequest;
import com.tkfc.core.common.pojo.Pagination;
import com.tkfc.core.common.pojo.PhysicallyDeleted;
import com.tkfc.core.common.pojo.Sort;
import com.tkfc.core.constants.StringPool;
import com.tkfc.core.throwable.RunTimException;
import com.tkfc.core.throwable.base.Assert;
import com.tkfc.core.throwable.base.ErrorCode;
import com.tkfc.core.toolkit.CollectionUtil;
import com.tkfc.core.toolkit.StringUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * sql 包装类 抽象类
 * 第四层抽象
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/2/22 18:15
 */
public abstract class ExpressionWrapper<Self, Field> extends AbstractWrapper<Self, Field> implements SqlExpression<Self> {


    private final static String EXPRESSION_SPLIT = "\\^";
    private final static String EXPRESSION_AND = "&";
    private final static String EXPRESSION_START = "start";
    private final static String EXPRESSION_ERROR_MESSAGE = " expression illegal ,  \n\n[example] :\n [ start^app_name^eq^admin ]\n [ or^age^bt^11&22 ]\n [ and^age^isNull^empty ]\n";
    private final static int EXPRESSION_LENGTH = 4;
    private final static boolean EXPRESSION_CONDITION = true;

    @Override
    public Self expression(BasePageExpressionRequest basePageRequest, String... allowFields) {
        expression(basePageRequest.getExpressions(), allowFields);
        setSort(basePageRequest.getSort());
        setPagination(basePageRequest.getPagination());
        return instance();
    }

    @Override
    public Self expression(String[] expressions, String... allowFields) {
        if (Objects.nonNull(expressions) && expressions.length > 0) {
            long hasStart = Arrays.stream(expressions).filter(s -> s.contains(EXPRESSION_START)).count();
            //如果表达式没有start开头 添加start开头
            if (hasStart < 1) {
                String expression = expressions[0];
                expression = EXPRESSION_START + expression.substring(expression.indexOf(StringPool.HAT));
                expressions[0] = expression;
            }
            //先取 start开头
            for (String expression : expressions) {
                if (expression.startsWith(EXPRESSION_START)) {
                    interpreterExpression(expression, allowFields);
                }
            }
            //后取 非start开头
            for (String expression : expressions) {
                if (!expression.startsWith(EXPRESSION_START)) {
                    interpreterExpression(expression, allowFields);
                }
            }
        }
        return instance();
    }

    @Override
    public Self expression(String[] expressions, Sort sort, String... allowFields) {
        expression(expressions, allowFields);
        setSort(sort);
        return instance();
    }

    @Override
    public Self expression(String[] expressions, Pagination pagination, String... allowFields) {
        expression(expressions, allowFields);
        setPagination(pagination);
        return instance();
    }

    @Override
    public Self expression(String[] expressions, Pagination pagination, Sort sort, String... allowFields) {
        expression(expressions, allowFields);
        setSort(sort);
        setPagination(pagination);
        return instance();
    }

    @Override
    public Self expression(String[] expressions, PhysicallyDeleted physicallyDeleted, String... allowFields) {
        expression(expressions, allowFields);
        setPhysicallyDeleted(physicallyDeleted);
        return instance();
    }

    @Override
    public Self expression(String[] expressions, Sort sort, PhysicallyDeleted physicallyDeleted, String... allowFields) {
        expression(expressions, allowFields);
        setSort(sort);
        setPhysicallyDeleted(physicallyDeleted);
        return instance();
    }

    @Override
    public Self expression(String[] expressions, Pagination pagination, PhysicallyDeleted physicallyDeleted, String... allowFields) {
        expression(expressions, allowFields);
        setPagination(pagination);
        setPhysicallyDeleted(physicallyDeleted);
        return instance();
    }

    @Override
    public Self expression(String[] expressions, Pagination pagination, Sort sort, PhysicallyDeleted physicallyDeleted, String... allowFields) {
        expression(expressions, allowFields);
        setSort(sort);
        setPagination(pagination);
        setPhysicallyDeleted(physicallyDeleted);
        return instance();
    }

    @Override
    public Self expression(BasePageExpressionRequest basePageRequest) {
        return expression(basePageRequest, new String[0]);
    }

    @Override
    public Self expression(String[] expressions) {
        return expression(expressions, new String[0]);
    }

    @Override
    public Self expression(String[] expressions, Sort sort) {
        return expression(expressions, sort, new String[0]);
    }

    @Override
    public Self expression(String[] expressions, Pagination pagination) {
        return expression(expressions, pagination, new String[0]);
    }

    @Override
    public Self expression(String[] expressions, Pagination pagination, Sort sort) {
        return expression(expressions, pagination, sort, new String[0]);
    }

    @Override
    public Self expression(String[] expressions, PhysicallyDeleted physicallyDeleted) {
        return expression(expressions, physicallyDeleted, new String[0]);
    }

    @Override
    public Self expression(String[] expressions, Sort sort, PhysicallyDeleted physicallyDeleted) {
        return expression(expressions, sort, physicallyDeleted, new String[0]);
    }

    @Override
    public Self expression(String[] expressions, Pagination pagination, PhysicallyDeleted physicallyDeleted) {
        return expression(expressions, pagination, physicallyDeleted, new String[0]);
    }

    @Override
    public Self expression(String[] expressions, Pagination pagination, Sort sort, PhysicallyDeleted physicallyDeleted) {
        return expression(expressions, pagination, sort, physicallyDeleted, new String[0]);
    }

    private void interpreterExpression(String expression, String... allowFields) {
        String[] expressionSplit = expression.split(EXPRESSION_SPLIT);
        Assert.isEquals(expressionSplit.length, EXPRESSION_LENGTH, EXPRESSION_ERROR_MESSAGE);
        SqlSpliceType splice = Optional.of(expressionSplit).map(e -> e[0]).map(SqlSpliceType::getByKeyword).orElse(SqlSpliceType.START);
        SqlOperatorType operator = Optional.of(expressionSplit).map(e -> e[2]).map(SqlOperatorType::getByKeyword).orElse(SqlOperatorType.UN_KNOW);
        String field = Optional.of(expressionSplit).map(e -> e[1]).map(StringUtil::camelCaseToUnderline).orElse(null);
        String value = Optional.of(expressionSplit).map(e -> e[3]).map(e -> e.replaceAll(StringPool.QUOTE, StringPool.EMPTY)).orElse(null);
        boolean notAllowField = Objects.nonNull(allowFields) && allowFields.length > 0 && Objects.equals(CollectionUtil.indexOfStringArray(allowFields, field), -1);
        Assert.notTrue(notAllowField, "not allow query filed %s", field);
        splice(splice, operator, field, value);
    }

    private void splice(SqlSpliceType splice, SqlOperatorType operator, String field, String value) {
        switch (splice) {

            case AND:
                and(EXPRESSION_CONDITION, s -> interpreter(operator, field, value));
                break;

            case AND_BODY:
                andBody(EXPRESSION_CONDITION, s -> interpreter(operator, field, value));
                break;

            case OR:
                or(EXPRESSION_CONDITION, s -> interpreter(operator, field, value));
                break;

            case OR_BODY:
                orBody(EXPRESSION_CONDITION, s -> interpreter(operator, field, value));
                break;

            case CONDITION:
                condition(EXPRESSION_CONDITION, s -> interpreter(operator, field, value));
                break;

            case EXISTS:
                exists(EXPRESSION_CONDITION, s -> interpreter(operator, field, value));
                break;

            case NOT_EXISTS:
                notExists(EXPRESSION_CONDITION, s -> interpreter(operator, field, value));
                break;

            case HAVING:
                having(EXPRESSION_CONDITION, s -> interpreter(operator, field, value));
                break;

            case START:
            case EMPTY:
                interpreter(operator, field, value);
                break;
            case UN_KNOW:
                throw new RunTimException(ErrorCode.OTHER.getCode(), EXPRESSION_ERROR_MESSAGE);

        }

    }

    private void interpreter(SqlOperatorType operator, String field, String value) {
        String[] values = value.split(EXPRESSION_AND);
        String[] valueArray = value.split(StringPool.COMMA);
        List<Object> valueList = Arrays.asList(valueArray);
        switch (operator) {
            case EQ:
                eq(EXPRESSION_CONDITION, field, value);
                break;

            case NEQ:
                neq(EXPRESSION_CONDITION, field, value);
                break;

            case LE:
                le(EXPRESSION_CONDITION, field, value);
                break;

            case LT:
                lt(EXPRESSION_CONDITION, field, value);
                break;

            case GE:
                ge(EXPRESSION_CONDITION, field, value);
                break;

            case GT:
                gt(EXPRESSION_CONDITION, field, value);
                break;

            case IN:
                in(EXPRESSION_CONDITION, field, valueList);
                break;

            case NOT_IN:
                notIn(EXPRESSION_CONDITION, field, valueList);
                break;

            case BETWEEN:
                Assert.notNull(values[0], EXPRESSION_ERROR_MESSAGE);
                Assert.notNull(values[1], EXPRESSION_ERROR_MESSAGE);
                between(EXPRESSION_CONDITION, field, values[0], values[1]);
                break;

            case NOT_BETWEEN:
                Assert.notNull(values[0], EXPRESSION_ERROR_MESSAGE);
                Assert.notNull(values[1], EXPRESSION_ERROR_MESSAGE);
                notBetween(EXPRESSION_CONDITION, field, values[0], values[1]);
                break;

            case LIKE:
                like(EXPRESSION_CONDITION, field, value);
                break;

            case NOT_LIKE:
                notLike(EXPRESSION_CONDITION, field, value);
                break;

            case LIKE_LEFT:
                likeLeft(EXPRESSION_CONDITION, field, value);
                break;

            case LIKE_RIGHT:
                likeRight(EXPRESSION_CONDITION, field, value);
                break;

            case IS_NULL:
                isNull(EXPRESSION_CONDITION, field);
                break;

            case IS_NOT_NULL:
                isNotNull(EXPRESSION_CONDITION, field);
                break;

            case WITH_DELETED:
                withDeleted(EXPRESSION_CONDITION);
                break;

            case CALL:
                call(EXPRESSION_CONDITION, field, value);
                break;

            case PRESS_DATE:
                pressDate(EXPRESSION_CONDITION, field, value);
                break;

            case PRESS_DATE_RANGE:
                Assert.notNull(values[0], EXPRESSION_ERROR_MESSAGE);
                Assert.notNull(values[1], EXPRESSION_ERROR_MESSAGE);
                pressDate(EXPRESSION_CONDITION, field, values[0], values[1]);
                break;

            case UN_KNOW:
                throw new RunTimException(ErrorCode.OTHER.getCode(), EXPRESSION_ERROR_MESSAGE);

        }
    }


}
