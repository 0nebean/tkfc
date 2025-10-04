package com.binance.client.model.user;

import lombok.Data;

import java.util.List;

/**
 * 杠杆分层标准
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/7/13 20:28
 */

@Data
public class LeverageBracket {
    private String symbol;
    private List<Bracket> brackets;

}
