package com.tkfc.core.enums;

import com.tkfc.core.enums.base.BaseEnums;

/**
 * 昨天今天明天 - 宋丹丹
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/2/22 23:13
 */
public enum YesterdayEnum implements BaseEnums<Integer> {

    //枚举项
    SAME_DAY(-1, "同一天", 0),
    YESTERDAY(0, "昨天", 1),
    LEAST_THE_DAY_BEFORE_YESTERDAY(1, "至少是前天", 2),
    ;

    private final String description;
    private final Integer value;
    private final Integer sort;

    YesterdayEnum(Integer value, String description, Integer sort) {
        this.value = value;
        this.description = description;
        this.sort = sort;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public Integer getSort() {
        return sort;
    }

    @Override
    public BaseEnums<Integer>[] getValues() {
        return values();
    }

}