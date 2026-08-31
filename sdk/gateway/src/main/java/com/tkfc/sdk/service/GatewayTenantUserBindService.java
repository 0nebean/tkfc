package com.tkfc.sdk.service;

import com.tkfc.boot.starter.mybatis.extend.BaseService;
import com.tkfc.sdk.model.GatewayTenantUserBind;
import com.tkfc.sdk.pojo.vo.GatewayTenantUserBindVo;


/**
 * 平台租户用户关联关系表 service
 *
 * @author 0neBean
 * @since 2023-03-24 20:28:02
 */
public interface GatewayTenantUserBindService extends BaseService<GatewayTenantUserBind, GatewayTenantUserBindVo> {

    /**
     * 将用户绑定到指定租户。
     *
     * @param ticketId 凭证ID，用于标识当前操作的上下文
     * @param tenantId 租户ID，表示要将用户绑定到的租户
     * @param userOpenId 用户的唯一标识符，用于识别待绑定的用户
     */
    void bindUserToTenant(String ticketId, String tenantId, String userOpenId);

}
