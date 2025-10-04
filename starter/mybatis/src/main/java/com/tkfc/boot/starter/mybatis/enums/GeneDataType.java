package com.tkfc.boot.starter.mybatis.enums;

import com.tkfc.core.enums.base.BaseEnums;

/**
 * 生成数据类型
 *
 * @author 0neBean
 * @version 1.0
 * @since 2024-09-29 16:35:38
 */
public enum GeneDataType implements BaseEnums<String> {

    //枚举项
    CRUD("0", "增删改查", 0),
    TREE("1", "树形结构", 1),
    ;

    private final String description;
    private final String value;
    private final Integer sort;

    GeneDataType(String value, String description, Integer sort) {
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