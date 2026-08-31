package com.tkfc.sdk.action;

import com.tkfc.core.common.annotations.web.action.Action;
import com.tkfc.core.common.annotations.web.method.json.PostJson;
import com.tkfc.core.common.annotations.web.param.BodyParam;
import com.tkfc.core.common.annotations.web.param.PathParam;
import com.tkfc.core.common.pojo.BasePageExpressionRequest;
import com.tkfc.core.common.pojo.BaseResponse;
import com.tkfc.sdk.model.GatewayTenant;
import com.tkfc.sdk.pojo.vo.GatewayTenantVo;
import com.tkfc.sdk.service.GatewayTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 网关租户
 *
 * @author 0neBean
 * @since 2026-07-06
 */
@RequiredArgsConstructor
@Action(tag = "网关租户", path = "gateway/tenant")
public class GatewayTenantAction {

    private final GatewayTenantService baseService;

    @PostJson(authors = {"0neBean"}, tag = "分页", path = {"page/{ticketId}"})
    public BaseResponse<List<GatewayTenantVo>> page(@PathVariable @PathParam(tag = "凭证ID") String ticketId, @BodyParam(tag = "通用列表请求对象") BasePageExpressionRequest request) {
        BaseResponse<List<GatewayTenant>> response = baseService.findGatewayTenantPage(ticketId, request);
        return BaseResponse.ok(baseService.toVos(response.getData()), response.getPagination());
    }

    @PostJson(authors = {"0neBean"}, tag = "详情", path = {"detail/{ticketId}/{id}"})
    public BaseResponse<GatewayTenantVo> detail(@PathVariable @PathParam(tag = "凭证ID") String ticketId, @PathVariable @PathParam(tag = "主键") Long id) {
        return BaseResponse.ok(baseService.findGatewayTenantById(id, ticketId).toVo(GatewayTenantVo.class));
    }

    @PostJson(authors = {"0neBean"}, tag = "保存", path = {"save/{ticketId}"})
    public Integer save(@PathVariable @PathParam(tag = "凭证ID") String ticketId, @BodyParam(tag = "实体类vo") GatewayTenantVo entity) {
        return baseService.saveGatewayTenant(entity, ticketId);
    }

    @PostJson(authors = {"0neBean"}, tag = "删除", path = {"delete/{ticketId}/{id}"})
    public Integer delete(@PathVariable @PathParam(tag = "凭证ID") String ticketId, @PathVariable @PathParam(tag = "主键") Long id) {
        return baseService.deleteGatewayTenantById(id, ticketId);
    }

}
