package com.tkfc.boot.starter.jsch.enums;

import com.tkfc.core.enums.base.BaseEnums;

/**
 * @author 0neBean
 * @version 1.0
 * @since 2021/2/22 23:13
 */
public enum PipeExecStatusEnum implements BaseEnums<Integer> {

    //枚举项
    TODO(0, "TODO", 0),
    DOING(1, "DOING", 1),
    FINISH(2, "FINISH", 2),
    FINISH_WITH_ERROR(3, "FINISH_WITH_ERROR", 3),
    ;

    private final String description;
    private final Integer value;
    private final Integer sort;

    PipeExecStatusEnum(Integer value, String description, Integer sort) {
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