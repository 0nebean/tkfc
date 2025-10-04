package com.tkfc.boot.starter.mybatis.sql.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum SqlOperatorType {


    EQ("eq"),
    NEQ("neq"),
    LE("le"),
    LT("lt"),
    GE("ge"),
    GT("gt"),
    IN("in"),
    NOT_IN("notIn"),
    BETWEEN("between"),
    NOT_BETWEEN("notBetween"),
    LIKE("like"),
    NOT_LIKE("notLike"),
    LIKE_LEFT("likeLeft"),
    LIKE_RIGHT("likeRight"),
    IS_NULL("isNull"),
    IS_NOT_NULL("isNotNull"),
    WITH_DELETED("withDeleted"),
    CALL("call"),
    PRESS_DATE("pressDate"),
    PRESS_DATE_RANGE("pressDateRange"),
    UN_KNOW("unKnow");


    @Getter
    private final String keyword;

    public static SqlOperatorType getByKeyword(String keyword) {
        for (SqlOperatorType s : SqlOperatorType.values()) {
            if (s.getKeyword().equals(keyword)) {
                return s;
            }
        }
        return UN_KNOW;
    }

}