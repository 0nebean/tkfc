package com.tkfc.sdk.pojo.base;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import lombok.*;

/**
 * 网关ws鉴权参数
 *
 * @author 0neBean
 * @version 1.0
 * @since 2024-09-02 22:25:43
 */
@Body(tag = "网关ws鉴权参数")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GatewayWsAuthParam {

    @BodyProperty(tag = "用户ID")
    private String userId;

    @BodyProperty(tag = "访问令牌")
    private String accessToken;

    @BodyProperty(tag = "设备标识")
    private String deviceToken;

    @BodyProperty(tag = "凭证标识")
    private String ticketId;

    @BodyProperty(tag = "用户名")
    private String username;

    @BodyProperty(tag = "租户ID")
    private String tenantId;

    @BodyProperty(tag = "用户姓名")
    private String realName;

    @BodyProperty(tag = "租户名称")
    private String tenantName;

    @BodyProperty(tag = "站点标识")
    private String siteKey;

    @BodyProperty(tag = "邮箱")
    private String email;

    @BodyProperty(tag = "手机号")
    private String mobile;
}
