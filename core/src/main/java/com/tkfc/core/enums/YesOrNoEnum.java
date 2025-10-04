package com.tkfc.core.enums;


import com.tkfc.core.enums.base.BaseEnums;

/**
 * 是否
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/2/22 23:13
 */
public enum YesOrNoEnum implements BaseEnums<String> {

    //枚举项
    NO("0", "否", 0),
    YES("1", "是", 1),
    ;

    private final String description;
    private final String value;
    private final Integer sort;

    YesOrNoEnum(String value, String description, Integer sort) {
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