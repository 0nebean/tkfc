package com.tkfc.boot.starter.opencv.model;

import com.tkfc.core.function.SerializableConsumer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图标点 颜色计数对象
 *
 * @author 0neBean
 * @version 1.0
 * @since 2022-01-30 00:40:53
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChartPointColorCount {

    private Integer count;
    private ChartPoint chartPoint;

}
