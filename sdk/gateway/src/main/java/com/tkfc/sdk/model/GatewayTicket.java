package com.tkfc.sdk.model;

import com.tkfc.boot.starter.mybatis.extend.BaseModel;
import com.tkfc.core.common.annotations.orm.FiledName;
import com.tkfc.core.common.annotations.orm.TableName;
import lombok.*;


/**
 * 凭证管理 model
 *
 * @author 0neBean
 * @since 2022-06-08 15:37:11
 */
@TableName("gateway_ticket")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GatewayTicket extends BaseModel {


    /**
     * 登录模式 0:验证码登录 1:账号密码登录
     */
    @FiledName("login_type")
    private String loginType;
    /**
     * 授权模式 0:服务器令牌,1:客户端令牌,2:ip白名单+静态令牌
     */
    @FiledName("auth_type")
    private String authType;
    /**
     * 静态令牌
     */
    @FiledName("static_token")
    private String staticToken;
    /**
     * 凭证标识
     */
    @FiledName("ticket_id")
    private String ticketId;
    /**
     * 登录验证 0：不验证 ， 1：短信验证码登录
     */
    @FiledName("login_verify")
    private String loginVerify;
    /**
     * 秘钥
     */
    @FiledName("secret")
    private String secret;
    /**
     * 凭证名称
     */
    @FiledName("ticket_name")
    private String ticketName;
    /**
     * 是否停用 0:否 1:是
     */
    @FiledName("is_lock")
    private String isLock;
    /**
     * oauth后重定向url
     */
    @FiledName("oauth_redirect_url")
    private String oauthRedirectUrl;
    /**
     * 一天内调用频次
     */
    @FiledName("call_times_pre_day")
    private Integer callTimesPreDay;
    /**
     * 当天内调用频次
     */
    @FiledName("call_times_current_day")
    private Integer callTimesCurrentDay;
    /**
     * 调用日期编码
     */
    @FiledName("call_times_press_date")
    private String callTimesPressDate;


}