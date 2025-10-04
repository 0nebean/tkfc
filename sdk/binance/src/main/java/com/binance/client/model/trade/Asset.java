package com.binance.client.model.trade;

import com.binance.client.constant.BinanceApiConstants;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;
@Data
public class Asset {

    private String asset;
    private BigDecimal walletBalance;
    private BigDecimal unrealizedProfit;
    private BigDecimal marginBalance;
    private BigDecimal maintMargin;
    private BigDecimal initialMargin;
    private BigDecimal positionInitialMargin;
    private BigDecimal openOrderInitialMargin;
    private BigDecimal crossWalletBalance;
    private BigDecimal crossUnPnl;
    private BigDecimal availableBalance;
    private BigDecimal maxWithdrawAmount;
    private Boolean marginAvailable;
}