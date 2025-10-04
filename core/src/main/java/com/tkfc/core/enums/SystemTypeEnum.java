package com.tkfc.core.enums;


import com.tkfc.core.enums.base.BaseEnums;

/**
 * 系统类型
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/2/22 23:13
 */
public enum SystemTypeEnum implements BaseEnums<String> {


    //枚举项
    WINDOWS("0", "windows", 0),
    LINUX("1", "linux", 1),
    MAC("2", "mac", 2),
    ;


    private final String description;
    private final String value;
    private final Integer sort;


    SystemTypeEnum(String value, String description, Integer sort) {
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