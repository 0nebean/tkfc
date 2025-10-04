package com.tkfc.boot.starter.mybatis.sql.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum SqlSpliceType {


    AND("and"),
    AND_BODY("andBody"),
    OR("or"),
    OR_BODY("orBody"),
    CONDITION("condition"),
    EXISTS("exists"),
    NOT_EXISTS("notExists"),
    HAVING("having"),
    EMPTY(""),
    START("start"),
    UN_KNOW("unKnow");

    @Getter
    private final String keyword;


    public static SqlSpliceType getByKeyword(String keyword) {
        for (SqlSpliceType s : SqlSpliceType.values()) {
            if (s.getKeyword().equals(keyword)) {
                return s;
            }
        }
        return UN_KNOW;
    }


}