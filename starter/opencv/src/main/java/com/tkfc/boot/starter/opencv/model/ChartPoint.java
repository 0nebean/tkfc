package com.tkfc.boot.starter.opencv.model;

import com.tkfc.core.function.SerializableConsumer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 图标点 对象
 *
 * @author 0neBean
 * @version 1.0
 * @since 2022-01-30 00:40:53
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChartPoint {

    private Integer x;
    private Integer y;
    private Double r;
    private Double g;
    private Double b;
    private SerializableConsumer<ChartPoint> matchHandler;

}
