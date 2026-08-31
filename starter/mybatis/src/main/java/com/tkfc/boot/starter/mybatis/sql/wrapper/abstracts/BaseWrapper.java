package com.tkfc.boot.starter.mybatis.sql.wrapper.abstracts;

import com.tkfc.boot.starter.mybatis.sql.enums.SqlKeyword;
import com.tkfc.boot.starter.mybatis.sql.wrapper.interfaces.IWrapper;
import com.tkfc.core.enums.base.BaseEnums;
import com.tkfc.core.toolkit.CollectionUtil;
import com.tkfc.core.toolkit.NumberUtil;
import com.tkfc.core.constants.StringPool;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * sql 包装类 抽象类
 * 第二层抽象
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/11/12 18:00
 */
public abstract class BaseWrapper<Self> implements IWrapper {

    /**
     * 获取包装类自己
     *
     * @return Self
     */
    protected abstract Self instance();


    /**
     * 字段转string 判断是否是数字 加上引号
     *
     * @return str
     */
    protected String valueToSqlString(Object value) {
        if (Objects.isNull(value)) {
            return StringPool.EMPTY;
        }
        if (CollectionUtil.isArray(value)) {
            List<String> values = Arrays.asList(value.toString().split(StringPool.COMMA));
            values = values.stream()
                    .map(s -> NumberUtil.isNum(s) ? s : SqlKeyword.SINGLE_QUOTE.getKeyword() + s + SqlKeyword.SINGLE_QUOTE.getKeyword())
                    .collect(Collectors.toList());
            return CollectionUtil.toStringWithOutSqBracket(values);
        }
        Object targetValue = normalizeEnumValue(value);
        return SqlKeyword.SINGLE_QUOTE.getKeyword() + targetValue + SqlKeyword.SINGLE_QUOTE.getKeyword();
    }

    /**
     * 将 IN / NOT IN 右侧的 Java 集合规范为「扁平」的元素列表。
     * <p>
     * 兼容历史误用：例如 {@code Collections.singletonList(真实id列表)} 会把 id 列表多包一层 List，
     * 此处会按层展开，直到首元素不再是 {@link Collection} 或只有一层标量元素为止。
     */
    protected List<?> normalizeInClauseValues(List<?> value) {
        if (value == null || value.isEmpty()) {
            return List.of();
        }
        List<?> cur = new ArrayList<>(value);
        while (cur.size() == 1) {
            Object only = cur.get(0);
            if (only instanceof Collection<?> inner) {
                cur = new ArrayList<>(inner);
            } else {
                break;
            }
        }
        return cur;
    }

    /**
     * 生成 IN / NOT IN 括号内的逗号分隔序列（不含左右括号），例如：{@code 1,2,'abc'}。
     * <p>
     * 与 {@link #valueToSqlString(Object)} 分离，避免对 List 使用 {@code toString().split(",")} 造成
     * {@code IN ('1,2,3')} 这类错误 SQL；标量格式化规则与历史 IN 分支尽量一致（数字不加引号，其余加单引号）。
     */
    protected String inCollectionToSqlEnumerable(List<?> value) {
        if (value == null || value.isEmpty()) {
            return StringPool.EMPTY;
        }
        List<?> flat = normalizeInClauseValues(value);
        return flat.stream().map(this::scalarToSqlInElement).collect(Collectors.joining(StringPool.COMMA));
    }

    /**
     * IN 子句中的单个元素字面量。
     */
    protected String scalarToSqlInElement(Object value) {
        if (Objects.isNull(value)) {
            return "NULL";
        }
        Object targetValue = normalizeEnumValue(value);
        if (targetValue instanceof Number) {
            return targetValue.toString();
        }
        String asStr = String.valueOf(targetValue);
        if (NumberUtil.isNum(asStr)) {
            return asStr;
        }
        return SqlKeyword.SINGLE_QUOTE.getKeyword() + asStr + SqlKeyword.SINGLE_QUOTE.getKeyword();
    }

    private Object normalizeEnumValue(Object value) {
        if (value instanceof BaseEnums<?> baseEnums) {
            return baseEnums.getValue();
        }
        if (value instanceof Enum<?>) {
            try {
                Method method = value.getClass().getMethod("getValue");
                return method.invoke(value);
            } catch (Exception ignored) {
                return value;
            }
        }
        return value;
    }

}
