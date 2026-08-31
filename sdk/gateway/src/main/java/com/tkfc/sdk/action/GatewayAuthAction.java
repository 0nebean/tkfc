package com.tkfc.sdk.action;

import com.tkfc.core.common.annotations.web.action.Action;
import com.tkfc.core.common.annotations.web.method.json.GetJson;
import com.tkfc.core.common.annotations.web.method.json.PostJson;
import com.tkfc.core.common.annotations.web.param.BodyParam;
import com.tkfc.core.common.annotations.web.report.IgnoreReportDoc;
import com.tkfc.sdk.biz.GatewayAuthBiz;
import com.tkfc.sdk.pojo.dto.*;
import com.tkfc.sdk.pojo.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 网关鉴权
 *
 * @author 0neBean
 * @since 2023-03-23 21:51:24
 */
@Slf4j
@RequiredArgsConstructor
@Action(tag = "网关鉴权", path = "auth")
public class GatewayAuthAction {

    private final GatewayAuthBiz authBizServe;

    @IgnoreReportDoc
    @PostJson(authors = {"0neBean"}, tag = "获取OAUTH2授权码", path = {"getAccessToken"})
    public AccessTokenVo getAccessToken(@BodyParam(tag = "获取访问令牌请求参数") GetAccessTokenReqDto req) {
        return authBizServe.getAccessToken(req);
    }

    @IgnoreReportDoc
    @PostJson(authors = {"0neBean"}, tag = "登录", path = {"loginLite"})
    public GatewayLoginVo loginLite(@BodyParam(tag = "网关的登录请求参数") GatewayLoginLiteReqDto req) {
        return authBizServe.loginLite(req);
    }

    @IgnoreReportDoc
    @PostJson(authors = {"0neBean"}, tag = "登出", path = {"logout"})
    public GatewayLogoutVo logout() {
        return authBizServe.logout();
    }

    @IgnoreReportDoc
    @GetJson(authors = {"0neBean"}, tag = "登录状态查询", path = {"loginStatus"})
    public GatewayLoginStatusVo loginStatus() {
        return authBizServe.loginStatus();
    }

    @IgnoreReportDoc
    @PostJson(authors = {"0neBean"}, tag = "登录", path = {"login"})
    public GatewayLoginVo login(@BodyParam(tag = "网关的登录请求参数") GatewayLoginReqDto req) {
        return authBizServe.login(req);
    }

    @IgnoreReportDoc
    @PostJson(authors = {"0neBean"}, tag = "发送短信验证码", path = {"sendSmsCode"})
    public Boolean sendSmsCode(@BodyParam(tag = "校验验证码请求参数") GatewayLoginCommonReqDto req) {
        return authBizServe.sendSmsCode(req);
    }

    @IgnoreReportDoc
    @PostJson(authors = {"0neBean"}, tag = "校验短信验证码", path = {"smsCodeCheck"})
    public Boolean smsCodeCheck(@BodyParam(tag = "校验验证码请求参数") GatewaySmsCodeCheckReqDto req) {
        return authBizServe.smsCodeCheck(req);
    }

    @IgnoreReportDoc
    @PostJson(authors = {"0neBean"}, tag = "查询租户信息", path = {"getTenantInfo"})
    public List<GatewayTenantVo> getTenantInfo(@BodyParam(tag = "查询租户信息") GatewayLoginCommonReqDto req) {
        return authBizServe.getTenantInfo(req);
    }

    @IgnoreReportDoc
    @PostJson(authors = {"0neBean"}, tag = "选中租户信息", path = {"selectTenant"})
    public Boolean selectTenant(@BodyParam(tag = "选中租户信息") GatewaySelectTenantReqDto req) {
        return authBizServe.selectTenant(req);
    }

    @IgnoreReportDoc
    @PostJson(authors = {"0neBean"}, tag = "注册客户账号", path = {"registerCustomer"})
    public Boolean registerCustomer(@BodyParam(tag = "注册客户账号参数") RegisterCustomerReqDto req) {
        return authBizServe.registerCustomer(req);
    }

    @IgnoreReportDoc
    @PostJson(authors = {"0neBean"}, tag = "注册客户账号-发送短信验证码", path = {"sendRegisterCustomerSms"})
    public Boolean sendRegisterCustomerSms(@BodyParam(tag = "注册客户账号-发送短信验证码参数") SendRegisterCustomerSmsReqDto req) {
        return authBizServe.sendRegisterCustomerSms(req);
    }

}
