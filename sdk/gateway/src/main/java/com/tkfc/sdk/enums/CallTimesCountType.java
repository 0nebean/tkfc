package com.tkfc.sdk.enums;

import com.tkfc.core.enums.base.BaseEnums;

public enum CallTimesCountType implements BaseEnums<String> {
    TICKET("0", "凭证", 0),
    API("1", "接口", 1),

    ;

    private final String description;
    private final String value;
    private final Integer sort;

    CallTimesCountType(String value, String description, Integer sort) {
        this.value = value;
        this.description = description;
        this.sort = sort;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public Integer getSort() {
        return sort;
    }

    @Override
    public BaseEnums<String>[] getValues() {
        return values();
    }


}
