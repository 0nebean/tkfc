package com.tkfc.core.enums;


import com.tkfc.core.enums.base.BaseEnums;

/**
 * 系统类型
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/2/22 23:13
 */
public enum OsTypeEnum implements BaseEnums<String> {


    //枚举项
    IOS("0", "IOS", 0),
    MAC("1", "MAC", 1),
    ANDROID("2", "ANDROID", 2),
    WINDOWS("3", "WINDOWS", 3),
    LINUX("4", "LINUX", 4),
    OTHER("5", "OTHER", 5),
    ;


    private final String description;
    private final String value;
    private final Integer sort;


    OsTypeEnum(String value, String description, Integer sort) {
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