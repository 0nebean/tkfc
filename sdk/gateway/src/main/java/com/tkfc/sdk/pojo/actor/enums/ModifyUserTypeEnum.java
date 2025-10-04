package com.tkfc.sdk.pojo.actor.enums;

import com.tkfc.core.enums.base.BaseEnums;

/**
 * 变更用户信息类型枚举
 *
 * @author 0neBean
 */
public enum ModifyUserTypeEnum implements BaseEnums<String> {

    ADD("0", "新增用户", 0),
    RESET_PASSWORD("1", "充值密码", 1),
    UPDATE_OPEN_ID("2", "更新用户ID", 2),
    UPDATE_USER_INFO("3", "变更用户信息", 3),
    DELETE_USERS("4", "删除用户", 4),
    ;

    private final String description;
    private final String value;
    private final Integer sort;

    ModifyUserTypeEnum(String value, String description, Integer sort) {
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
