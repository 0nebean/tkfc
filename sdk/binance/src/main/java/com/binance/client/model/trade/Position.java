package com.binance.client.model.trade;

import com.binance.client.constant.BinanceApiConstants;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;
@Data
public class Position {

    private String symbol;
    private BigDecimal initialMargin;
    private BigDecimal maintMargin;
    private BigDecimal unrealizedProfit;
    private BigDecimal positionInitialMargin;
    private BigDecimal openOrderInitialMargin;
    private Integer leverage;
    private Boolean isolated;
    private BigDecimal entryPrice;
    private BigDecimal maxNotional;
    private String positionSide;
    private BigDecimal positionAmt;

}
