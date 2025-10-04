package com.tkfc.sdk.pojo.vo;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import lombok.*;

/**
 * 网关的登出请求参数
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
@Body(tag = "网关的登出请求参数")
public class GatewayLogoutVo {

    @BodyProperty(tag = "单点登录地址")
    private String ssoAddress;

}
