package com.tkfc.sdk.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.tkfc.boot.starter.mybatis.extend.BaseServiceImpl;
import com.tkfc.boot.starter.mybatis.extend.BaseVo;
import com.tkfc.boot.starter.mybatis.sql.build.SqlBuilder;
import com.tkfc.boot.starter.mybatis.sql.wrapper.SqlWrapper;
import com.tkfc.cache.base.interfaces.ICacheService;
import com.tkfc.core.enums.YesOrNoEnum;
import com.tkfc.core.toolkit.JsonUtil;
import com.tkfc.sdk.mapper.GatewayTicketMapper;
import com.tkfc.sdk.model.GatewayTicket;
import com.tkfc.sdk.pojo.dto.BindApiTransferQueryDto;
import com.tkfc.sdk.pojo.vo.GatewayApiLiteVo;
import com.tkfc.sdk.pojo.vo.GatewayApiVo;
import com.tkfc.sdk.pojo.vo.GatewayTicketVo;
import com.tkfc.sdk.service.GatewayTicketApiBindService;
import com.tkfc.sdk.service.GatewayTicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 凭证管理 serviceImpl
 *
 * @author 0neBean
 * @since 2022-06-08 15:37:11
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GatewayTicketServiceImpl extends BaseServiceImpl<GatewayTicket, GatewayTicketVo, GatewayTicketMapper> implements GatewayTicketService {

    private final ICacheService cacheService;
    private final GatewayTicketApiBindService bindService;


    @Override
    public GatewayTicket findByTicketId(String ticketId) {
        SqlWrapper<GatewayTicket> sql = SqlBuilder.<GatewayTicket>init();
        sql.eq(GatewayTicket::getTicketId, ticketId);
        sql.eq(GatewayTicket::getIsLock, YesOrNoEnum.NO.getValue());
        return findOne(sql);
    }

    @Override
    public Boolean syncAccessInfo(Long id) {
        //生成缓存key
        GatewayTicket ticketInfo = findById(id);
        String groupKey = String.format("gateway:accessInfo:%s", ticketInfo.getTicketId());
        String ticketInfoKey = String.format("%s:ticketInfo", groupKey);
        String pathInfoKey = String.format("%s:apiPathMapApiInfo", groupKey);
        String ticketMapApiIdsKey = String.format("%s:ticketMapApiIds", groupKey);
        //查询改应用关联数据
        BindApiTransferQueryDto param = new BindApiTransferQueryDto();
        param.setMainDataId(id);
        List<GatewayApiVo> bindAllApi = bindService.findBindApiInfo(param);
        List<GatewayApiLiteVo> bindAllApiLite = JsonUtil.copyList(bindAllApi, GatewayApiLiteVo.class);
        Map<String, GatewayApiLiteVo> apiPathMapApiId = bindAllApiLite.stream().collect(Collectors.toMap(GatewayApiLiteVo::getProxyPath, Function.identity()));
        List<Long> apiIds = bindAllApi.stream().map(BaseVo::getId).collect(Collectors.toList());
        //转换凭证信息
        GatewayTicketVo ticketVo = ticketInfo.toVo(GatewayTicketVo.class);
        JSONObject ticketJson = JsonUtil.toJsonObject(ticketVo);
        //设置缓存
        cacheService.set(ticketInfoKey, ticketJson);
        cacheService.set(pathInfoKey, apiPathMapApiId);
        cacheService.set(ticketMapApiIdsKey, apiIds);
        return Boolean.TRUE;
    }
}
