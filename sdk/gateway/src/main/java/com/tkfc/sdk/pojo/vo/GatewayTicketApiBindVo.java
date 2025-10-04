package com.tkfc.sdk.pojo.vo;

import com.tkfc.boot.starter.mybatis.extend.BaseVo;
import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import com.tkfc.sdk.model.GatewayTicketApiBind;
import lombok.*;


/**
 * 凭证绑定接口关系 vo
 *
 * @author 0neBean
 * @since 2022-07-20 22:08:33
 */
@Body(tag = "凭证绑定接口关系")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GatewayTicketApiBindVo extends BaseVo<GatewayTicketApiBind> {

    /**
     * 凭证ID
     */
    @BodyProperty(tag = "凭证ID")
    private Long ticket;
    /**
     * 接口ID
     */
    @BodyProperty(tag = "接口ID")
    private Long apiId;
    /**
     * 是否需要登录
     */
    @BodyProperty(tag = "是否需要登录")
    private String needLogin;
    /**
     * 一小时内调用频次
     */
    @BodyProperty(tag = "一小时内调用频次")
    private Integer callTimesPreHour;

    /**
     * 当前一小时内调用频次
     */
    @BodyProperty(tag = "当前一小时内调用频次")
    private Integer callTimesCurrentHour;

    /**
     * 调用的小时编码
     */
    @BodyProperty(tag = "调用的小时编码")
    private String callTimesPressHour;

}
