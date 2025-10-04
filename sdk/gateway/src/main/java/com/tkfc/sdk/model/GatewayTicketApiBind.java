package com.tkfc.sdk.model;

import com.tkfc.boot.starter.mybatis.extend.BaseModel;
import com.tkfc.core.common.annotations.orm.FiledName;
import com.tkfc.core.common.annotations.orm.TableName;
import lombok.*;


/**
 * 凭证绑定接口关系 model
 *
 * @author 0neBean
 * @since 2022-07-20 22:08:33
 */
@TableName("gateway_ticket_api_bind")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GatewayTicketApiBind extends BaseModel {


    /**
     * 凭证ID
     */
    @FiledName("ticket_id")
    private Long ticketId;
    /**
     * 接口ID
     */
    @FiledName("api_id")
    private Long apiId;

    /**
     * 是否需要登录
     */
    @FiledName("need_login")
    private String needLogin;

    /**
     * 一小时内调用频次
     */
    @FiledName("call_times_pre_hour")
    private Integer callTimesPreHour;

    /**
     * 当前一小时内调用频次
     */
    @FiledName("call_times_current_hour")
    private Integer callTimesCurrentHour;

    /**
     * 调用的小时编码
     */
    @FiledName("call_times_press_hour")
    private String callTimesPressHour;


}