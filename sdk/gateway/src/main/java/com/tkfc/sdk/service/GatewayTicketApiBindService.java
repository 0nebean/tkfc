package com.tkfc.sdk.service;

import com.tkfc.boot.starter.mybatis.extend.BaseService;
import com.tkfc.sdk.model.GatewayTicketApiBind;
import com.tkfc.sdk.pojo.dto.BindApiTransferQueryDto;
import com.tkfc.sdk.pojo.vo.GatewayApiVo;
import com.tkfc.sdk.pojo.vo.GatewayTicketApiBindVo;

import java.util.List;


/**
 * 凭证绑定接口关系 service
 *
 * @author 0neBean
 * @since 2022-07-20 22:08:33
 */
public interface GatewayTicketApiBindService extends BaseService<GatewayTicketApiBind, GatewayTicketApiBindVo> {

    /**
     * 分页查询凭证绑定的接口信息
     *
     * @param request 请求参数
     * @return 分页api信息
     */
    List<GatewayApiVo> findBindApiInfo(BindApiTransferQueryDto request);

    /**
     * 根据凭证ID和接口ID查询绑定
     *
     * @param ticketId 凭证ID
     * @param apiId    接口ID
     * @return 绑定信息
     */
    GatewayTicketApiBind findByTicketIdAndApiId(Long ticketId, String apiId);
}
