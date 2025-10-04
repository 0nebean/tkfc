package com.tkfc.welus.definition.enums;

/**
 * 返回类型
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/1/8 9:46
 */
public enum ReturnType {

    JSON("JSON"),
    VIEW("VIEW"),
    ;

    ReturnType() {
    }

    private ReturnType(String key) {
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
