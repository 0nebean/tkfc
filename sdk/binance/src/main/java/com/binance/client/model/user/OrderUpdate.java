package com.binance.client.model.user;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class OrderUpdate {

    public OrderUpdate(String symbol, String clientOrderId) {
        this.symbol = symbol;
        this.clientOrderId = clientOrderId;
    }

    private String symbol;

    private String clientOrderId;

    private String side;

    private String type;

    private String timeInForce;

    private BigDecimal origQty;

    private BigDecimal price;

    private BigDecimal avgPrice;

    private BigDecimal stopPrice;

    private String executionType;

    private String orderStatus;

    private Long orderId;

    private BigDecimal lastFilledQty;

    private BigDecimal cumulativeFilledQty;

    private BigDecimal lastFilledPrice;

    private String commissionAsset;

    private BigDecimal commissionAmount;

    private Long orderTradeTime;

    private Long tradeID;

    private BigDecimal bidsNotional;

    private BigDecimal asksNotional;

    private Boolean isMarkerSide;

    private Boolean isReduceOnly;

    private String workingType;

    private BigDecimal activationPrice;

    private BigDecimal callbackRate;

}
