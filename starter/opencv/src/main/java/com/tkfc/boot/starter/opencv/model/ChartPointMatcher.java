package com.tkfc.boot.starter.opencv.model;

import com.tkfc.core.function.SerializableCiConsumer;
import com.tkfc.core.function.SerializableConsumer;
import com.tkfc.core.function.SerializableFunction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
public class ChartPointMatcher {

    private List<ChartPoint> chartPoints;
    SerializableFunction<ChartPoint,ChartPoint> matchHandler;

}
