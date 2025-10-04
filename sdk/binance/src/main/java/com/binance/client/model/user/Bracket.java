package com.binance.client.model.user;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Bracket {

    private Integer bracket;
    private Integer initialLeverage;
    private BigDecimal notionalCap;
    private BigDecimal notionalFloor;
    private BigDecimal maintMarginRatio;
    private Integer cum;
}
