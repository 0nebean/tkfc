package com.tkfc.sdk.pojo.actor;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import com.tkfc.sdk.pojo.actor.enums.GatewaySyncTypeEnum;
import lombok.*;

/**
 * 同步网关消息
 *
 * @author 0neBean
 * @version 1.0
 * @since 2024-08-13 19:15:32
 */
@Body(tag = "同步网关消息")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SyncGatewayInfoMsg {

    @BodyProperty(tag = "同步类型")
    private GatewaySyncTypeEnum syncType;

    @BodyProperty(tag = "凭证ID")
    private Long ticketId;

}
