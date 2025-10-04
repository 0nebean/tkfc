package com.tkfc.welus.definition.enums;

/**
 * 返回类型
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/1/8 9:46
 */
public enum CovertParamType {

    REQ("REQ"),
    RESP("RESP"),
    ;

    CovertParamType() {
    }

    private CovertParamType(String key) {
        this.key = key;
    }


    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
