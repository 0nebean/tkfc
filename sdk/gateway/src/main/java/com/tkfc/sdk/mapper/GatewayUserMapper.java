package com.tkfc.sdk.mapper;

import com.tkfc.boot.starter.mybatis.extend.BaseSplitMapper;
import com.tkfc.sdk.model.GatewayUser;

/**
 * 网关用户 Mapper
 *
 * @author 0neBean
 * @since 2024-06-03 18:11:07
 */

public interface GatewayUserMapper extends BaseSplitMapper<GatewayUser> {

    /**
     * 检查是否存在与给定票证ID相对应的表。
     *
     * @param ticketId 要检查的机票的ID
     * @return true if the table exists, false otherwise
     */
    Boolean tableExists(String ticketId);


}
