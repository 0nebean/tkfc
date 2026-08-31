package com.tkfc.sdk.service;

import com.tkfc.boot.starter.mybatis.extend.BaseService;
import com.tkfc.core.common.pojo.BasePageExpressionRequest;
import com.tkfc.core.common.pojo.BaseResponse;
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

    /**
     * 查找未绑定角色的租户
     *
     * @param request  参数
     * @param ticketId ticket id
     * @return list
     */
    List<GatewayTenantVo> findUnBindTenantInfo(TransferQueryDto request, String ticketId);

    /**
     * 查询分页的租户信息。
     *
     * @param ticketId 凭证ID
     * @param request  分页请求参数
     * @return 包含租户信息列表的响应对象
     */
    BaseResponse<List<GatewayTenant>> findGatewayTenantPage(String ticketId, BasePageExpressionRequest request);

    /**
     * 查询租户详情。
     *
     * @param id       租户ID
     * @param ticketId 凭证ID
     * @return 租户信息
     */
    GatewayTenant findGatewayTenantById(Long id, String ticketId);

    /**
     * 保存租户信息。
     *
     * @param entity   租户信息
     * @param ticketId 凭证ID
     * @return 影响的行数
     */
    Integer saveGatewayTenant(GatewayTenantVo entity, String ticketId);

    /**
     * 删除租户信息。
     *
     * @param id       租户ID
     * @param ticketId 凭证ID
     * @return 影响的行数
     */
    Integer deleteGatewayTenantById(Long id, String ticketId);
}
