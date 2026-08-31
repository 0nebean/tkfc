package com.tkfc.sdk.service;

import com.tkfc.boot.starter.mybatis.extend.BaseService;
import com.tkfc.sdk.model.GatewayCallTimes;
import com.tkfc.sdk.pojo.dto.BatchQueryCallTimesReqDto;
import com.tkfc.sdk.pojo.vo.GatewayCallTimesVo;

import java.util.List;


/**
 * 调用统计 service
 *
 * @author 0neBean
 * @since 2024-12-19
 */
public interface GatewayCallTimesService extends BaseService<GatewayCallTimes, GatewayCallTimesVo> {

    /**
     * 根据凭证ID和接口ID查找API调用统计信息。
     *
     * @param ticketId 凭证ID
     * @param apiId    接口ID
     * @return 返回与给定凭证ID和接口ID相关的调用统计信息，如果未找到匹配项，则返回null。
     */
    GatewayCallTimes findApiCallTimes(String ticketId, Long apiId);

    /**
     * 根据凭证ID查找相关的API调用统计信息。
     *
     * @param ticketId 凭证ID
     * @return 返回与给定凭证ID相关的调用统计信息，如果未找到匹配项，则返回null。
     */
    GatewayCallTimes findTicketCallTimes(String ticketId);

    /**
     * 批量查询凭证类型调用统计
     *
     * @param request 批量查询请求参数
     * @return 调用统计列表
     */
    List<GatewayCallTimesVo> batchFindTicketCallTimes(BatchQueryCallTimesReqDto request);

    /**
     * 批量查询接口类型调用统计
     *
     * @param request 批量查询请求参数
     * @return 调用统计列表
     */
    List<GatewayCallTimesVo> batchFindApiCallTimes(BatchQueryCallTimesReqDto request);
}

