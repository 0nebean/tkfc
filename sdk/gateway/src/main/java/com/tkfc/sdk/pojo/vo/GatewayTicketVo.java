package com.tkfc.sdk.pojo.vo;

import com.tkfc.boot.starter.mybatis.extend.BaseVo;
import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import com.tkfc.core.common.annotations.web.response.Dictionary;
import com.tkfc.sdk.model.GatewayTicket;
import lombok.*;


/**
 * 凭证管理 vo
 *
 * @author 0neBean
 * @since 2022-06-08 15:37:11
 */
@Body(tag = "凭证管理")
@Getter
@AllArgsConstructor
@Setter
@NoArgsConstructor
@Builder
public class GatewayTicketVo extends BaseVo<GatewayTicket> {

    /**
     * 登录模式 0:验证码登录 1:账号密码登录
     */
    @Dictionary(groupVal = "WGDLMS")
    @BodyProperty(tag = "登录模式 0:验证码登录 1:账号密码登录")
    private String loginType;
    /**
     * 授权模式 0:服务器令牌,1:客户端令牌,2:ip白名单+静态令牌
     */
    @Dictionary(groupVal = "WGSQMS")
    @BodyProperty(tag = "授权模式 0:服务器令牌,1:客户端令牌,2:ip白名单+静态令牌")
    private String authType;
    /**
     * 静态令牌
     */
    @BodyProperty(tag = "静态令牌")
    private String staticToken;
    /**
     * 凭证标识
     */
    @BodyProperty(tag = "凭证标识")
    private String ticketId;
    /**
     * 秘钥
     */
    @BodyProperty(tag = "秘钥")
    private String secret;

    /**
     * 凭证名称
     */
    @BodyProperty(tag = "凭证名称")
    private String ticketName;
    /**
     * 登录验证 0：不验证 ， 1：短信验证码登录
     */
    @BodyProperty(tag = "登录验证 0：不验证 ， 1：短信验证码登录")
    private String loginVerify;
    /**
     * 是否停用 0:否 1:是
     */
    @Dictionary(groupVal = "SF")
    @BodyProperty(tag = "是否停用 0:否 1:是")
    private String isLock;

    /**
     * oauth后重定向url
     */
    @BodyProperty(tag = "oauth后重定向url")
    private String oauthRedirectUrl;

    /**
     * 一天内调用频次
     */
    @BodyProperty(tag = "一天内调用频次")
    private String callTimesPreDay;
    /**
     * 当天内调用频次
     */
    @BodyProperty(tag = "当天内调用频次")
    private Integer callTimesCurrentDay;
    /**
     * 调用日期编码
     */
    @BodyProperty(tag = "调用日期编码")
    private String callTimesPressDate;

}
