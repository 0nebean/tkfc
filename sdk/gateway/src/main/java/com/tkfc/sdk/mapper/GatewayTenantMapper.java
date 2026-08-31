package com.tkfc.sdk.mapper;

import com.tkfc.boot.starter.mybatis.extend.BaseMapper;
import com.tkfc.core.common.pojo.Pagination;
import com.tkfc.sdk.model.GatewayTenant;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 平台租户信息 Mapper
 *
 * @author 0neBean
 * @since 2023-03-24 20:55:14
 */

public interface GatewayTenantMapper extends BaseMapper<GatewayTenant> {


    /**
     * 查找已经绑定角色的用户
     *
     * @param userId     用户ID
     * @param tenantName 租户姓名
     * @param ticketId   ticket id
     * @param pagination 分页
     * @return list
     */
    List<GatewayTenant> findBindTenantInfo(@Param("userId") Long userId, @Param("tenantName") String tenantName, @Param("ticketId") String ticketId, @Param("pagination") Pagination pagination);

    /**
     * 查找未绑定角色的用户
     *
     * @param userId     用户ID
     * @param tenantName 租户姓名
     * @param ticketId ticket id
     * @param pagination 分页
     * @return list
     */
    List<GatewayTenant> findUnBindTenantInfo(@Param("userId") Long userId, @Param("tenantName") String tenantName,@Param("ticketId")  String ticketId, @Param("pagination") Pagination pagination);
}
