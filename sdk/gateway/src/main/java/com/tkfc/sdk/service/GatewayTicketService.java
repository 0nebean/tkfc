package com.tkfc.sdk.service;

import com.tkfc.boot.starter.mybatis.extend.BaseService;
import com.tkfc.sdk.model.GatewayTicket;
import com.tkfc.sdk.pojo.vo.GatewayTicketVo;


/**
 * 凭证管理 service
 *
 * @author 0neBean
 * @since 2022-06-08 15:37:11
 */
public interface GatewayTicketService extends BaseService<GatewayTicket, GatewayTicketVo> {


    /**
     * 根据凭证ID查询查询凭证信息
     *
     * @param ticketId 凭证ID
     * @return 凭证信息
     */
    GatewayTicket findByTicketId(String ticketId);

    /**
     * 同步凭证访问信息到网关缓存
     *
     * @param id 凭证ID
     * @return bool
     */
    Boolean syncAccessInfo(Long id);
}
