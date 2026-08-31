package com.tkfc.sdk.service.impl;

import com.tkfc.boot.starter.mybatis.extend.BaseServiceImpl;
import com.tkfc.boot.starter.mybatis.sql.build.SqlBuilder;
import com.tkfc.boot.starter.mybatis.sql.wrapper.SqlWrapper;
import com.tkfc.core.toolkit.CollectionUtil;
import com.tkfc.sdk.enums.CallTimesCountType;
import com.tkfc.sdk.mapper.GatewayCallTimesMapper;
import com.tkfc.sdk.model.GatewayCallTimes;
import com.tkfc.sdk.pojo.dto.BatchQueryCallTimesReqDto;
import com.tkfc.sdk.pojo.vo.GatewayCallTimesVo;
import com.tkfc.sdk.service.GatewayCallTimesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 调用统计 serviceImpl
 *
 * @author 0neBean
 * @since 2024-12-19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GatewayCallTimesServiceImpl extends BaseServiceImpl<GatewayCallTimes, GatewayCallTimesVo, GatewayCallTimesMapper> implements GatewayCallTimesService {


    @Override
    public GatewayCallTimes findApiCallTimes(String ticketId, Long apiId) {
        SqlWrapper<GatewayCallTimes> sql = SqlBuilder.init();
        sql.eq(GatewayCallTimes::getTicketId, ticketId);
        sql.eq(GatewayCallTimes::getApiId, apiId);
        sql.eq(GatewayCallTimes::getCountType, CallTimesCountType.API.getValue());
        GatewayCallTimes callTimes = findOne(sql);
        if (Objects.isNull(callTimes)) {
            callTimes = new GatewayCallTimes();
            callTimes.setTicketId(ticketId);
            callTimes.setApiId(apiId);
            callTimes.setCountType(CallTimesCountType.API.getValue());
        }
        return callTimes;
    }

    @Override
    public GatewayCallTimes findTicketCallTimes(String ticketId) {
        SqlWrapper<GatewayCallTimes> sql = SqlBuilder.init();
        sql.eq(GatewayCallTimes::getTicketId, ticketId);
        sql.eq(GatewayCallTimes::getCountType, CallTimesCountType.TICKET.getValue());
        GatewayCallTimes callTimes = findOne(sql);
        if (Objects.isNull(callTimes)) {
            callTimes = new GatewayCallTimes();
            callTimes.setTicketId(ticketId);
            callTimes.setCountType(CallTimesCountType.TICKET.getValue());
        }
        return callTimes;
    }

    @Override
    public List<GatewayCallTimesVo> batchFindTicketCallTimes(BatchQueryCallTimesReqDto request) {
        if (CollectionUtil.isEmpty(request.getTicketIds())) {
            return new ArrayList<>();
        }
        SqlWrapper<GatewayCallTimes> sql = SqlBuilder.init();
        sql.in(GatewayCallTimes::getTicketId, request.getTicketIds());
        sql.eq(GatewayCallTimes::getCountType, CallTimesCountType.TICKET.getValue());
        List<GatewayCallTimes> callTimesList = find(sql);
        return toVos(callTimesList);
    }

    @Override
    public List<GatewayCallTimesVo> batchFindApiCallTimes(BatchQueryCallTimesReqDto request) {
        if (CollectionUtil.isEmpty(request.getTicketIds())) {
            return new ArrayList<>();
        }
        SqlWrapper<GatewayCallTimes> sql = SqlBuilder.init();
        sql.eq(GatewayCallTimes::getTicketId, request.getTicketIds().get(0));
        sql.in(GatewayCallTimes::getApiId, request.getApiIds());
        sql.eq(GatewayCallTimes::getCountType, CallTimesCountType.API.getValue());
        List<GatewayCallTimes> callTimesList = find(sql);
        return toVos(callTimesList);
    }
}

