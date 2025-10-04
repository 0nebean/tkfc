package com.tkfc.sdk.pojo.vo;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import lombok.*;

/**
 * 网关的登录请求参数
 *
 * @author 0neBean
 * @since 2023-03-24 18:42:40
 */
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Body(tag = "网关的登录请求参数")
public class GatewayLoginVo {

    @BodyProperty(tag = "身份令牌")
    private String identityToken;

    @BodyProperty(tag = "手机号")
    private String mobileNumber;

    @BodyProperty(tag = "登录校验类型")
    private String loginVerify;

}
