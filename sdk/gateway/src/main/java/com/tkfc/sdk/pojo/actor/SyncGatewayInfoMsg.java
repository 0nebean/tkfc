package com.tkfc.sdk.pojo.actor;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import com.tkfc.sdk.pojo.actor.enums.GatewaySyncTypeEnum;
import com.tkfc.sdk.pojo.vo.GatewayApiLiteVo;
import com.tkfc.sdk.pojo.vo.GatewayTicketVo;
import lombok.*;

import java.util.List;
import java.util.Map;

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

    @BodyProperty(tag = "凭证信息")
    private GatewayTicketVo ticketVo;

    @BodyProperty(tag = "请求地址map接口信息")
    private Map<String, GatewayApiLiteVo> apiPathMapApiId;

    @BodyProperty(tag = "接口IDS")
    private List<Long> apiIds;

    @BodyProperty(tag = "域名map站点key")
    private Map<String, Object> domainMapSiteKey;

    @BodyProperty(tag = "域名列表")
    private List<String> domainList;

    @BodyProperty(tag = "同步类型")
    private GatewaySyncTypeEnum syncType;

    @BodyProperty(tag = "凭证ID")
    private Long ticketId;

}
