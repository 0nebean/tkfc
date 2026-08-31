package com.tkfc.sdk.service.impl;

import com.tkfc.boot.starter.mybatis.extend.BaseServiceImpl;
import com.tkfc.boot.starter.mybatis.sql.build.SqlBuilder;
import com.tkfc.boot.starter.mybatis.sql.wrapper.SqlWrapper;
import com.tkfc.core.common.pojo.BasePageExpressionRequest;
import com.tkfc.core.common.pojo.BaseResponse;
import com.tkfc.sdk.mapper.GatewayTenantMapper;
import com.tkfc.sdk.model.GatewayTenant;
import com.tkfc.sdk.pojo.dto.TransferQueryDto;
import com.tkfc.sdk.pojo.vo.GatewayTenantVo;
import com.tkfc.sdk.service.GatewayTenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 平台租户信息 serviceImpl
 *
 * @author 0neBean
 * @since 2023-03-24 20:55:14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GatewayTenantServiceImpl extends BaseServiceImpl<GatewayTenant, GatewayTenantVo, GatewayTenantMapper> implements GatewayTenantService {
    @Override
    public List<GatewayTenantVo> findBindTenantInfo(TransferQueryDto request, String ticketId) {
        List<GatewayTenant> tenantInfo = baseMapper.findBindTenantInfo(request.getMainDataId(), request.getSearchText(), ticketId, request.getPagination());
        return toVos(tenantInfo);
    }

    @Override
    public List<GatewayTenantVo> findUnBindTenantInfo(TransferQueryDto request, String ticketId) {
        List<GatewayTenant> tenantInfo = baseMapper.findUnBindTenantInfo(request.getMainDataId(), request.getSearchText(), ticketId, request.getPagination());
        return toVos(tenantInfo);
    }

    @Override
    public BaseResponse<List<GatewayTenant>> findGatewayTenantPage(String ticketId, BasePageExpressionRequest request) {
        SqlWrapper<GatewayTenant> sql = SqlBuilder.<GatewayTenant>init().expression(request);
        return BaseResponse.ok(find(sql), sql.getPagination());
    }

    @Override
    public GatewayTenant findGatewayTenantById(Long id, String ticketId) {
        return findById(id);
    }

    @Override
    public Integer saveGatewayTenant(GatewayTenantVo entity, String ticketId) {
        return save(entity.toModel());
    }

    @Override
    public Integer deleteGatewayTenantById(Long id, String ticketId) {
        return deleteById(id);
    }

}
