package com.tkfc.sdk.enums;

import com.tkfc.core.enums.base.BaseEnums;

public enum GatewayAuthType implements BaseEnums<String> {
    OAUTH("0", "OAUTH2授权码", 0),
    OAUTH_LOGIN_CHECK("1", "OAUTH2授权码 + 登录校验", 1),
    OAUTH_LOGIN_LITE_CHECK("2", "OAUTH2授权码 + 轻登录校验", 2),
    OAUTH_DEVICE_TOKEN_LOGIN_CHECK("3", "OAUTH2授权码 + 令牌登录校验", 3),
    ;

    private final String description;
    private final String value;
    private final Integer sort;

    GatewayAuthType(String value, String description, Integer sort) {
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
