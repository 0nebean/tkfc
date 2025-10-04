package com.tkfc.sdk.biz;

import com.tkfc.cache.base.interfaces.ICacheService;
import com.tkfc.sdk.model.GatewayTicket;
import com.tkfc.sdk.model.GatewayTicketApiBind;
import com.tkfc.sdk.pojo.dto.GatewayNoticeMsg;
import com.tkfc.sdk.service.GatewayTicketApiBindService;
import com.tkfc.sdk.service.GatewayTicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 网关鉴权领域服务
 *
 * @author 0neBean
 * @since 2023-03-23 23:28:48
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GatewayNoticeBiz {

    private final ICacheService cacheService;
    private final GatewayTicketService ticketService;
    private final GatewayTicketApiBindService apiBindService;


    /**
     * 更新调用限制信息
     *
     * @param msg 网关通知消息
     */
    public void updateCallTimesInfo(GatewayNoticeMsg msg) {
        GatewayTicket ticket = ticketService.findByTicketId(msg.getTicketId());
        if (Objects.isNull(ticket)) {
            return;
        }
        switch (msg.getMsgType()) {
            case "apiCallTimesRefreshKey":
                GatewayTicketApiBind apiBind = apiBindService.findByTicketIdAndApiId(ticket.getId(), msg.getApiId());
                if (Objects.isNull(apiBind)) {
                    return;
                }
                apiBind.setCallTimesPressHour(msg.getDateCode());
                apiBind.setCallTimesCurrentHour(cacheService.getInteger(msg.getRedisKey()));
                apiBindService.save(apiBind);
                break;
            case "ticketCallTimesRefreshKey":
                ticket.setCallTimesPressDate(msg.getDateCode());
                ticket.setCallTimesCurrentDay(cacheService.getInteger(msg.getRedisKey()));
                ticketService.save(ticket);
                break;
        }
    }

}
