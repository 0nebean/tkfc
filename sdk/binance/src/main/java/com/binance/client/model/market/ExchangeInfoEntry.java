package com.binance.client.model.market;

import com.binance.client.constant.BinanceApiConstants;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;
import java.util.Map;

import java.math.BigDecimal;

public class ExchangeInfoEntry {

    private Long deliveryDate;

    private Long onboardDate;

    private String pair;

    private String symbol;

    private String status;

    private BigDecimal maintMarginPercent;

    private BigDecimal requiredMarginPercent;

    private String baseAsset;

    private String quoteAsset;

    private String marginAsset;

    private Long pricePrecision;

    private Long quantityPrecision;

    private Long baseAssetPrecision;

    private Long quotePrecision;

    private List<String> orderTypes;

    private List<String> timeInForce;

    private String contractType;

    private String underlyingType;

    private Integer settlePlan;

    private BigDecimal triggerProtect;

    private List<String> underlyingSubType;

    private List<List<Map<String, String>>> filters;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getMaintMarginPercent() {
        return maintMarginPercent;
    }

    public void setMaintMarginPercent(BigDecimal maintMarginPercent) {
        this.maintMarginPercent = maintMarginPercent;
    }

    public BigDecimal getRequiredMarginPercent() {
        return requiredMarginPercent;
    }

    public void setRequiredMarginPercent(BigDecimal requiredMarginPercent) {
        this.requiredMarginPercent = requiredMarginPercent;
    }

    public String getBaseAsset() {
        return baseAsset;
    }

    public void setBaseAsset(String baseAsset) {
        this.baseAsset = baseAsset;
    }

    public String getQuoteAsset() {
        return quoteAsset;
    }

    public void setQuoteAsset(String quoteAsset) {
        this.quoteAsset = quoteAsset;
    }

    public Long getPricePrecision() {
        return pricePrecision;
    }

    public void setPricePrecision(Long pricePrecision) {
        this.pricePrecision = pricePrecision;
    }

    public Long getQuantityPrecision() {
        return quantityPrecision;
    }

    public void setQuantityPrecision(Long quantityPrecision) {
        this.quantityPrecision = quantityPrecision;
    }

    public Long getBaseAssetPrecision() {
        return baseAssetPrecision;
    }

    public void setBaseAssetPrecision(Long baseAssetPrecision) {
        this.baseAssetPrecision = baseAssetPrecision;
    }

    public Long getQuotePrecision() {
        return quotePrecision;
    }

    public void setQuotePrecision(Long quotePrecision) {
        this.quotePrecision = quotePrecision;
    }

    public List<String> getOrderTypes() {
        return orderTypes;
    }

    public void setOrderTypes(List<String> orderTypes) {
        this.orderTypes = orderTypes;
    }

    public List<String> getTimeInForce() {
        return timeInForce;
    }

    public void setTimeInForce(List<String> timeInForce) {
        this.timeInForce = timeInForce;
    }

    public List<List<Map<String, String>>> getFilters() {
        return filters;
    }

    public void setFilters(List<List<Map<String, String>>> filters) {
        this.filters = filters;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getPair() {
        return pair;
    }

    public void setPair(String pair) {
        this.pair = pair;
    }

    public Long getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Long deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Long getOnboardDate() {
        return onboardDate;
    }

    public void setOnboardDate(Long onboardDate) {
        this.onboardDate = onboardDate;
    }

    public String getMarginAsset() {
        return marginAsset;
    }

    public void setMarginAsset(String marginAsset) {
        this.marginAsset = marginAsset;
    }

    public String getUnderlyingType() {
        return underlyingType;
    }

    public void setUnderlyingType(String underlyingType) {
        this.underlyingType = underlyingType;
    }

    public Integer getSettlePlan() {
        return settlePlan;
    }

    public void setSettlePlan(Integer settlePlan) {
        this.settlePlan = settlePlan;
    }

    public BigDecimal getTriggerProtect() {
        return triggerProtect;
    }

    public void setTriggerProtect(BigDecimal triggerProtect) {
        this.triggerProtect = triggerProtect;
    }

    public List<String> getUnderlyingSubType() {
        return underlyingSubType;
    }

    public void setUnderlyingSubType(List<String> underlyingSubType) {
        this.underlyingSubType = underlyingSubType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, BinanceApiConstants.TO_STRING_BUILDER_STYLE).append("symbol", symbol)
                .append("status", status).append("maintMarginPercent", maintMarginPercent)
                .append("contractType", contractType)
                .append("requiredMarginPercent", requiredMarginPercent).append("baseAsset", baseAsset)
                .append("quoteAsset", quoteAsset).append("pricePrecision", pricePrecision)
                .append("quantityPrecision", quantityPrecision).append("baseAssetPrecision", baseAssetPrecision)
                .append("quotePrecision", quotePrecision).append("orderTypes", orderTypes)
                .append("timeInForce", timeInForce).append("filters", filters).toString();
    }
}
