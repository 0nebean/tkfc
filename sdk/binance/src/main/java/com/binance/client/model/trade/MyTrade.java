package com.binance.client.model.trade;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MyTrade {

    private Long id;

    private Boolean isBuyer;

    private BigDecimal commission;

    private String commissionAsset;

    private Long counterPartyId;

    private Boolean isMaker;

    private Long orderId;

    private String marginAsset;

    private BigDecimal price;

    private BigDecimal qty;

    private BigDecimal quoteQty;

    private BigDecimal realizedPnl;

    private String side;

    private String positionSide;

    private String symbol;

    private Long time;
}
