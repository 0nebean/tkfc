package com.tkfc.sdk.pojo.dto;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import com.tkfc.core.common.pojo.Pagination;
import lombok.*;

/**
 * 穿梭框列表查询入参
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/6/4 14:20
 */
@Body(tag = "穿梭框列表查询入参")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferQueryDto {

    @BodyProperty(tag = "主数据ID")
    private Long mainDataId;

    @BodyProperty(tag = "搜索条件字段")
    private String searchText;

    @BodyProperty(tag = "分页")
    private Pagination pagination;
}
