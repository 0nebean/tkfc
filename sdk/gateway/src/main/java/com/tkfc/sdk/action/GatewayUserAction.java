package com.tkfc.sdk.action;

import com.tkfc.boot.starter.mybatis.sql.build.SqlBuilder;
import com.tkfc.boot.starter.mybatis.sql.wrapper.SqlWrapper;
import com.tkfc.core.common.annotations.web.action.Action;
import com.tkfc.core.common.annotations.web.method.json.PostJson;
import com.tkfc.core.common.annotations.web.param.BodyParam;
import com.tkfc.core.common.annotations.web.param.PathParam;
import com.tkfc.core.common.pojo.BasePageExpressionRequest;
import com.tkfc.core.common.pojo.BaseResponse;
import com.tkfc.sdk.model.GatewayUser;
import com.tkfc.sdk.pojo.base.TransferBindDto;
import com.tkfc.sdk.pojo.dto.TransferQueryDto;
import com.tkfc.sdk.pojo.vo.GatewayTenantVo;
import com.tkfc.sdk.pojo.vo.GatewayUserVo;
import com.tkfc.sdk.service.GatewayTenantService;
import com.tkfc.sdk.service.GatewayUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 网关鉴权
 *
 * @author 0neBean
 * @since 2023-03-23 21:51:24
 */
@RequiredArgsConstructor
@Action(tag = "网关鉴权", path = "gateway/user")
public class GatewayUserAction {

    private final GatewayUserService baseService;
    private final GatewayTenantService tenantService;

    @PostJson(authors = {"0neBean"}, tag = "分页", path = {"page/{ticketId}"})
    public BaseResponse<List<GatewayUser>> page(@PathVariable @PathParam(tag = "凭证ID") String ticketId, @BodyParam(tag = "通用列表请求对象") BasePageExpressionRequest request) {
        return baseService.findGatewayUserPage(ticketId, request);
    }

    @PostJson(authors = {"0neBean"}, tag = "未绑定租户数据", path = {"findUnBindTenantInfo/{ticketId}"})
    public BaseResponse<List<GatewayTenantVo>> findUnBindTenantInfo(@PathVariable @PathParam(tag = "凭证ID") String ticketId, @BodyParam(tag = "通用列表请求对象") TransferQueryDto request) {
        return BaseResponse.ok(tenantService.findUnBindTenantInfo(request, ticketId), request.getPagination());
    }

    @PostJson(authors = {"0neBean"}, tag = "绑定租户数据", path = {"findBindTenantInfo/{ticketId}"})
    public BaseResponse<List<GatewayTenantVo>> findBindTenantInfo(@PathVariable @PathParam(tag = "凭证ID") String ticketId, @BodyParam(tag = "通用列表请求对象") TransferQueryDto request) {
        return BaseResponse.ok(tenantService.findBindTenantInfo(request, ticketId), request.getPagination());
    }

    @PostJson(authors = {"0neBean"}, tag = "绑定租户客户", path = {"bindUserTenant/{ticketId}"})
    public BaseResponse<Boolean> bindUserTenant(@PathVariable @PathParam(tag = "凭证ID") String ticketId, @BodyParam(tag = "绑定租户客户参数") TransferBindDto request) {
        return BaseResponse.ok(baseService.bindUserTenant(request, ticketId));
    }

    @PostJson(authors = {"0neBean"}, tag = "解除绑定租户客户", path = {"unBindUserTenant/{ticketId}"})
    public BaseResponse<Boolean> unBindUserTenant(@PathVariable @PathParam(tag = "凭证ID") String ticketId, @BodyParam(tag = "解除绑定租户客户参数") TransferBindDto request) {
        return BaseResponse.ok(baseService.unBindUserTenant(request, ticketId));
    }

}
