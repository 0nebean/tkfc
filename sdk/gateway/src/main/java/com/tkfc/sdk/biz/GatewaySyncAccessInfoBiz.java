package com.tkfc.sdk.biz;

import com.tkfc.cache.base.interfaces.ICacheService;
import com.tkfc.sdk.model.GatewayCallTimes;
import com.tkfc.sdk.pojo.actor.SyncGatewayInfoMsg;
import com.tkfc.sdk.pojo.dto.GatewayNoticeMsg;
import com.tkfc.sdk.service.GatewayCallTimesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 网关同步访问信息领域服务
 *
 * @author 0neBean
 * @since 2023-03-23 23:28:48
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GatewaySyncAccessInfoBiz {

    private final ICacheService cacheService;
    private final GatewayCallTimesService callTimesService;


    /**
     * 更新调用限制信息
     *
     * @param msg 网关通知消息
     */
    public void updateCallTimesInfo(GatewayNoticeMsg msg) {
        GatewayCallTimes callTimes;
        switch (msg.getMsgType()) {
            case "apiCallTimesRefreshKey":
                callTimes = callTimesService.findApiCallTimes(msg.getTicketId(), msg.getApiId());
                callTimes.setDataCode(msg.getDateCode());
                callTimes.setCallTimes(cacheService.getInteger(msg.getRedisKey()));
                if (Objects.nonNull(callTimes.getCallTimes()) && callTimes.getCallTimes() > 0) {
                    callTimesService.save(callTimes);
                }
                break;
            case "ticketCallTimesRefreshKey":
                callTimes = callTimesService.findTicketCallTimes(msg.getTicketId());
                callTimes.setDataCode(msg.getDateCode());
                callTimes.setCallTimes(cacheService.getInteger(msg.getRedisKey()));
                if (Objects.nonNull(callTimes.getCallTimes()) && callTimes.getCallTimes() > 0) {
                    callTimesService.save(callTimes);
                }
                break;
        }
    }

    private final static String HOST_MAP_SITE_IDS_KEY = "gateway:siteInfo:hostMapSiteIds";

    /**
     * 同步网关信息并返回登录响应。
     *
     * @param req 包含同步类型和凭证ID的请求消息
     * @return bool
     */
    public Boolean siteSync(SyncGatewayInfoMsg req) {
        for (String key : req.getDomainMapSiteKey().keySet()) {
            cacheService.hSet(HOST_MAP_SITE_IDS_KEY, key, req.getDomainMapSiteKey().get(key));
        }
        return true;
    }

    /**
     * 删除指定网关信息并返回登录响应。
     *
     * @param req 包含同步类型和凭证ID的请求消息
     * @return bool
     */
    public Boolean siteDel(SyncGatewayInfoMsg req) {
        for (String key : req.getDomainList()) {
            cacheService.hDel(HOST_MAP_SITE_IDS_KEY, key);
        }
        return true;
    }

    public Boolean syncTicketInfo(SyncGatewayInfoMsg msg) {
        //设置缓存
        String groupKey = String.format("gateway:accessInfo:%s", msg.getTicketVo().getTicketId());
        String ticketInfoKey = String.format("%s:ticketInfo", groupKey);
        String pathInfoKey = String.format("%s:apiPathMapApiInfo", groupKey);
        String ticketMapApiIdsKey = String.format("%s:ticketMapApiIds", groupKey);
        cacheService.set(ticketInfoKey, msg.getTicketVo());
        cacheService.set(pathInfoKey, msg.getApiPathMapApiId());
        cacheService.set(ticketMapApiIdsKey, msg.getApiIds());
        return Boolean.TRUE;
    }
}
