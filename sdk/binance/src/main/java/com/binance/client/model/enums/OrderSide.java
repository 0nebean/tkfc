package com.binance.client.model.enums;

import lombok.Getter;

/**
 * buy, sell, both.
 */

public enum OrderSide {
  BUY("BUY"),
  SELL("SELL");

  @Getter
  private final String code;

  OrderSide(String side) {
    this.code = side;
  }

  @Override
  public String toString() {
    return code;
  }

  public static OrderSide getKeyByValue(String value) {
    for (OrderSide s : OrderSide.values()) {
      if (s.getCode().equals(value)) {
        return s;
      }
    }
    return null;
  }


}