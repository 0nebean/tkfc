package com.tkfc.boot.starter.mybatis.sql.enums;

import com.tkfc.core.constants.StringPool;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum SqlKeyword {
    AND("AND"),
    OR("OR"),
    NOT("NOT"),
    IN("IN"),
    NOT_IN("NOT IN"),
    LIKE("LIKE"),
    NOT_LIKE("NOT LIKE"),
    EQ(StringPool.EQUALS),
    NE("<>"),
    GT(StringPool.RIGHT_CHEV),
    GE(">="),
    LT(StringPool.LEFT_CHEV),
    LE("<="),
    IS_NULL("IS NULL"),
    IS_NOT_NULL("IS NOT NULL"),
    GROUP_BY("GROUP BY"),
    HAVING("HAVING"),
    ORDER_BY("ORDER BY"),
    EXISTS("EXISTS"),
    NOT_EXISTS("NOT EXISTS"),
    BETWEEN("BETWEEN"),
    NOT_BETWEEN("NOT BETWEEN"),
    ASC("ASC"),
    SPACE(StringPool.SPACE),
    LEFT_BRACKET(StringPool.LEFT_BRACKET),
    RIGHT_BRACKET(StringPool.RIGHT_BRACKET),
    PERCENT(StringPool.PERCENT),
    SINGLE_QUOTE(StringPool.SINGLE_QUOTE),
    COMMA(StringPool.COMMA),
    DESC("DESC");


    @Getter
    private final String keyword;


}