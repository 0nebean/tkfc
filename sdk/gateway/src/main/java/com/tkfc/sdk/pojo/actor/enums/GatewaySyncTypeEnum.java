package com.tkfc.sdk.pojo.actor.enums;

import com.tkfc.core.enums.base.BaseEnums;

/**
 * 同步站点信息类型枚举
 *
 * @author 0neBean
 */
public enum GatewaySyncTypeEnum implements BaseEnums<String> {

    SYNC_TICKET_INFO("0", "同步凭证信息", 0),
    SYNC_SITE_INFO("1", "同步站点信息", 1),
    ;

    private final String description;
    private final String value;
    private final Integer sort;

    GatewaySyncTypeEnum(String value, String description, Integer sort) {
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
