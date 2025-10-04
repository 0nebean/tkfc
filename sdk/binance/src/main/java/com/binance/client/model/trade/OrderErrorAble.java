package com.binance.client.model.trade;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 下单失败
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/8/1 21:17
 */
public class OrderErrorAble extends Order {

    private Integer code;
    private String msg;

    public OrderErrorAble() {
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
