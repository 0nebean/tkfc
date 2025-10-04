package com.tkfc.sdk.service;

import com.tkfc.boot.starter.mybatis.extend.BaseService;
import com.tkfc.sdk.model.GatewayTenant;
import com.tkfc.sdk.pojo.dto.TransferQueryDto;
import com.tkfc.sdk.pojo.vo.GatewayTenantVo;

import java.util.List;


/**
 * 平台租户信息 service
 *
 * @author 0neBean
 * @since 2023-03-24 20:55:14
 */
public interface GatewayTenantService extends BaseService<GatewayTenant, GatewayTenantVo> {

    /**
     * 查找已经绑定角色的租户
     *
     * @param request  参数
     * @param ticketId ticket id
     * @return list
     */
    List<GatewayTenantVo> findBindTenantInfo(TransferQueryDto request, String ticketId);
}
