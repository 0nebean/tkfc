package com.tkfc.sdk.pojo.dto;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import com.tkfc.sdk.pojo.base.BaseBindTransferQueryDto;
import lombok.Getter;
import lombok.Setter;

/**
 * 绑定接口穿梭框列表查询入参
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/6/4 14:20
 */
@Body(tag = "绑定接口穿梭框列表查询入参")
@Getter
@Setter
public class BindApiTransferQueryDto extends BaseBindTransferQueryDto {

    @BodyProperty(tag = "应用标识")
    private String appKey;

}
