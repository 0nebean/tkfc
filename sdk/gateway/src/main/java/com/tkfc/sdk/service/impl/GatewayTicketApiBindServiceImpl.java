package com.tkfc.sdk.service.impl;

import com.tkfc.boot.starter.mybatis.extend.BaseServiceImpl;
import com.tkfc.boot.starter.mybatis.sql.build.SqlBuilder;
import com.tkfc.boot.starter.mybatis.sql.wrapper.SqlWrapper;
import com.tkfc.sdk.mapper.GatewayTicketApiBindMapper;
import com.tkfc.sdk.model.GatewayTicketApiBind;
import com.tkfc.sdk.pojo.dto.BindApiTransferQueryDto;
import com.tkfc.sdk.pojo.vo.GatewayApiVo;
import com.tkfc.sdk.pojo.vo.GatewayTicketApiBindVo;
import com.tkfc.sdk.service.GatewayTicketApiBindService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 凭证绑定接口关系 serviceImpl
 *
 * @author 0neBean
 * @since 2022-07-20 22:08:33
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GatewayTicketApiBindServiceImpl extends BaseServiceImpl<GatewayTicketApiBind, GatewayTicketApiBindVo, GatewayTicketApiBindMapper> implements GatewayTicketApiBindService {

    @Override
    public List<GatewayApiVo> findBindApiInfo(BindApiTransferQueryDto request) {
        return baseMapper.findBindApiInfo(
                request.getMainDataId(),
                request.getAppKey(),
                request.getSearchText(),
                request.getPagination()
        );
    }

    @Override
    public GatewayTicketApiBind findByTicketIdAndApiId(Long ticketId, String apiId) {
        SqlWrapper<GatewayTicketApiBind> sql = SqlBuilder.init();
        sql.eq(GatewayTicketApiBind::getTicketId,ticketId);
        sql.eq(GatewayTicketApiBind::getApiId,apiId);
        return findOne(sql);
    }
}
