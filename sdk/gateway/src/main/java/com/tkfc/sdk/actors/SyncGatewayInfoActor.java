package com.tkfc.sdk.actors;


import com.tkfc.boot.starter.mus.abstracts.AbstractActor;
import com.tkfc.boot.starter.mus.annotations.Actor;
import com.tkfc.core.toolkit.JsonUtil;
import com.tkfc.sdk.consts.GlobalConst;
import com.tkfc.sdk.pojo.actor.SyncGatewayInfoMsg;
import com.tkfc.sdk.pojo.actor.enums.GatewaySyncTypeEnum;
import com.tkfc.sdk.service.GatewaySiteService;
import com.tkfc.sdk.service.GatewayTicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 同步网关信息
 */
@Slf4j
@RequiredArgsConstructor
@Actor(topic = GlobalConst.ActorTopic.TOPIC_SYNC_GATEWAY_INFO,group = GlobalConst.ActorTopicGroup.DEFAULT_GROUP)
public class SyncGatewayInfoActor extends AbstractActor {

    private final GatewaySiteService siteService;
    private final GatewayTicketService ticketService;

    @Override
    public void notice(byte[] message) {
        SyncGatewayInfoMsg msg = JsonUtil.toBean(message, SyncGatewayInfoMsg.class);
        log.info("SyncGatewayInfoActor got an new msg = {}", msg);
        GatewaySyncTypeEnum syncType = msg.getSyncType();
        switch (syncType) {
            case SYNC_TICKET_INFO:
                ticketService.syncAccessInfo(msg.getTicketId());
                break;
            case SYNC_SITE_INFO:
                siteService.sync();
                break;
            default:
                break;
        }
    }

}
