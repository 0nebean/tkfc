package com.binance.client.model.trade;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.binance.client.model.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 批量下单参数
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/8/1 19:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostBatchOrderDto {

    private String symbol;
    private OrderSide side;
    private PositionSide positionSide;
    private OrderType type;
    private Boolean reduceOnly;
    private BigDecimal quantity;
    private BigDecimal price;
    private String newClientOrderId;
    private BigDecimal stopPrice;
    private BigDecimal activationPrice;
    private BigDecimal callbackRate;
    private TimeInForce timeInForce;
    private WorkingType workingType;
    private Boolean priceProtect;
    private Boolean closePosition;
    private NewOrderRespType newOrderRespType;

    public static String toJsonParamString(List<PostBatchOrderDto> batchOrders) {
        JSONArray resultArray = new JSONArray();
        JSONArray jsonArray = JSONArray.parseArray(JSONArray.toJSONString(batchOrders));
        for (Object o : jsonArray) {
            JSONObject item = (JSONObject)o;
            JSONObject result = new JSONObject();
            for (Map.Entry<String, Object> entry : item.entrySet()) {
                result.put(entry.getKey(), entry.getValue().toString());
            }
            resultArray.add(result);
        }
        return JSONObject.toJSONString(resultArray,JSONWriter.Feature.PrettyFormat);
    }
}
