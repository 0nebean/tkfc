package com.tkfc.boot.starter.mybatis.enums;

import com.tkfc.core.enums.base.BaseEnums;

/**
 * 代码生成范围
 *
 * @author 0neBean
 * @version 1.0
 * @since 2024-09-29 16:35:48
 */
public enum GeneScope implements BaseEnums<String> {

    //枚举项
    SERVICE("0", "Service", 0),
    ACTION("1", "Controller", 1),
    PAGE("2", "页面", 2),
    ;

    private final String description;
    private final String value;
    private final Integer sort;

    GeneScope(String value, String description, Integer sort) {
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