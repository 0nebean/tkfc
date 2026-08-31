package com.tkfc.sdk.pojo.dto;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import com.tkfc.core.common.annotations.web.validation.NotEmptyCollection;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * 批量查询调用统计请求参数
 *
 * @author 0neBean
 * @since 2024-12-19
 */
@Body(tag = "批量查询调用统计请求参数")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BatchQueryCallTimesReqDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 凭证ID列表
     */
    @NotEmptyCollection
    @BodyProperty(tag = "凭证ID列表")
    private List<String> ticketIds;

    /**
     * 接口ID列表（仅用于API类型查询）
     */
    @BodyProperty(tag = "接口ID列表（仅用于API类型查询）")
    private List<Long> apiIds;

}

