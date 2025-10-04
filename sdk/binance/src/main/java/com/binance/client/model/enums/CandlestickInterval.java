package com.binance.client.model.enums;

/**
 * 1min, 5min, 15min, 30min, 60min, 1day, 1mon, 1week, 1year
 */
public enum CandlestickInterval {
    ONE_MINUTE("1m", 60 * 1000L),
    THREE_MINUTES("3m", 3 * 60 * 1000L),
    FIVE_MINUTES("5m", 5 * 60 * 1000L),
    FIFTEEN_MINUTES("15m", 15 * 60 * 1000L),
    HALF_HOURLY("30m", 30 * 60 * 1000L),
    HOURLY("1h", 60 * 60 * 1000L),
    TWO_HOURLY("2h", 2 * 60 * 60 * 1000L),
    FOUR_HOURLY("4h", 4 * 60 * 60 * 1000L),
    SIX_HOURLY("6h", 6 * 60 * 60 * 1000L),
    EIGHT_HOURLY("8h",8 * 60 * 60 * 1000L),
    TWELVE_HOURLY("12h", 12 * 60 * 60 * 1000L),
    DAILY("1d", 24 * 60 * 60 * 1000L),
    THREE_DAILY("3d",3 * 24 * 60 * 60 * 1000L),
    WEEKLY("1w", 7 * 24 * 60 * 60 * 1000L),
    MONTHLY("1M", 30 * 24 * 60 * 60 * 1000L);

    private final String code;

    private final Long millis;

    private Long getMillis() {
        return millis;
    }

    CandlestickInterval(String code, Long millis) {
        this.code = code;
        this.millis = millis;
    }

    @Override
    public String toString() {
        return code;
    }

    public static CandlestickInterval getValueByCode(String code) {
        for (CandlestickInterval value : values()) {
            if (value.toString().equals(code)) {
                return value;
            }
        }
        return null;
    }

    public static Long getMillisByCode(String code) {
        for (CandlestickInterval value : values()) {
            if (value.toString().equals(code)) {
                return value.getMillis();
            }
        }
        return null;
    }
}
