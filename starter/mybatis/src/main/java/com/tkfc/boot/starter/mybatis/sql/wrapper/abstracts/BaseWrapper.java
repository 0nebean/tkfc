package com.tkfc.boot.starter.mybatis.sql.wrapper.abstracts;

import com.tkfc.boot.starter.mybatis.sql.enums.SqlKeyword;
import com.tkfc.boot.starter.mybatis.sql.wrapper.interfaces.IWrapper;
import com.tkfc.core.toolkit.CollectionUtil;
import com.tkfc.core.toolkit.NumberUtil;
import com.tkfc.core.constants.StringPool;

import java.util.Arrays;
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
            values = values.stream().map(s -> NumberUtil.isNum(s) ? s : SqlKeyword.SINGLE_QUOTE.getKeyword() + s + SqlKeyword.SINGLE_QUOTE.getKeyword()).collect(Collectors.toList());
            return CollectionUtil.toStringWithOutSqBracket(values);
        }
        return SqlKeyword.SINGLE_QUOTE.getKeyword() + value + SqlKeyword.SINGLE_QUOTE.getKeyword();
    }


}
