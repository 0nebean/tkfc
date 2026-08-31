package com.tkfc.sdk.model;

import com.tkfc.boot.starter.mybatis.extend.BaseModel;
import com.tkfc.core.common.annotations.orm.FiledName;
import com.tkfc.core.common.annotations.orm.TableName;
import lombok.*;


/**
 * 调用统计 model
 *
 * @author 0neBean
 * @since 2024-12-19
 */
@TableName("gateway_call_times")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GatewayCallTimes extends BaseModel {


    /**
     * 对外暴露应用ID
     */
    @FiledName("ticket_id")
    private String ticketId;
    /**
     * 接口ID
     */
    @FiledName("api_id")
    private Long apiId;
    /**
     * 统计类型
     */
    @FiledName("count_type")
    private String countType;
    /**
     * 调用时间编码
     */
    @FiledName("data_code")
    private String dataCode;
    /**
     * 当前日内调用频次
     */
    @FiledName("call_times")
    private Integer callTimes;

}

