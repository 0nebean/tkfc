package com.tkfc.sdk.pojo.base;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import lombok.*;

import java.util.List;

/**
 * 穿梭框解绑定入参
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/6/4 16:22
 */
@Body(tag = "穿梭框解绑定入参")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferBindDto {


    @BodyProperty(tag = "主数据ID")
    private Long mainDataId;


    @BodyProperty(tag = "绑定的IDS")
    private List<Long> bindIds;


}
