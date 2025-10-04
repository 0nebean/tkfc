package com.binance.client.model.trade;

import com.binance.client.constant.BinanceApiConstants;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;

public class AssetExchangeInformation {

    private String asset;

    private String marginAvailable;

    private BigDecimal autoAssetExchange;

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }


    public String getMarginAvailable() {
        return marginAvailable;
    }

    public void setMarginAvailable(String marginAvailable) {
        this.marginAvailable = marginAvailable;
    }

    public BigDecimal getAutoAssetExchange() {
        return autoAssetExchange;
    }

    public void setAutoAssetExchange(BigDecimal autoAssetExchange) {
        this.autoAssetExchange = autoAssetExchange;
    }

    @Override
    public String toString() {
        return "AssetExchangeInformation{" +
                "asset='" + asset + '\'' +
                ", marginAvailable='" + marginAvailable + '\'' +
                ", autoAssetExchange=" + autoAssetExchange +
                '}';
    }
}
