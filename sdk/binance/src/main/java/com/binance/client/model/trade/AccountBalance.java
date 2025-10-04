package com.binance.client.model.trade;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountBalance {

    private String accountAlias;
    private String asset;
    private BigDecimal balance;
    private BigDecimal withdrawAvailable;
    private Long updateTime;

}
