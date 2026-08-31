package com.tkfc.sdk.pojo.vo;

import com.tkfc.boot.starter.mybatis.extend.BaseVo;
import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import com.tkfc.sdk.model.GatewayCallTimes;
import lombok.*;


/**
 * 调用统计 vo
 *
 * @author 0neBean
 * @since 2024-12-19
 */
@Body(tag = "调用统计")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GatewayCallTimesVo extends BaseVo<GatewayCallTimes> {

    /**
     * 对外暴露应用ID
     */
    @BodyProperty(tag = "对外暴露应用ID")
    private String ticketId;
    /**
     * 接口ID
     */
    @BodyProperty(tag = "接口ID")
    private Long apiId;
    /**
     * 统计类型
     */
    @BodyProperty(tag = "统计类型")
    private String countType;
    /**
     * 调用时间编码
     */
    @BodyProperty(tag = "调用时间编码")
    private String dataCode;
    /**
     * 当前日内调用频次
     */
    @BodyProperty(tag = "当前日内调用频次")
    private Integer callTimes;

}

