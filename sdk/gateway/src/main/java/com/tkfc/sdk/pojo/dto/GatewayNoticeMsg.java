package com.tkfc.sdk.pojo.dto;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 网关订阅消息
 *
 * @author 0neBean
 * @version 1.0
 * @since 2024-12-17 16:15:23
 */
@Getter
@Setter
@Builder
@ToString
@Body(tag = "网关订阅消息")
public class GatewayNoticeMsg {

    @BodyProperty(tag = "时间编码")
    private String dateCode;

    @BodyProperty(tag = "消息类型")
    private String msgType;

    @BodyProperty(tag = "接口ID")
    private String apiId;

    @BodyProperty(tag = "缓存key")
    private String redisKey;

    @BodyProperty(tag = "凭证ID")
    private String ticketId;

}
