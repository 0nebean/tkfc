package com.tkfc.sdk.enums;

import com.tkfc.core.enums.base.BaseEnums;

public enum GatewayTicketLoginVerifyType implements BaseEnums<String> {
    NONE("0", "不验证", 0),
    SMS_CODE("1", "短信验证码登录", 1),

    ;

    private final String description;
    private final String value;
    private final Integer sort;

    GatewayTicketLoginVerifyType(String value, String description, Integer sort) {
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
