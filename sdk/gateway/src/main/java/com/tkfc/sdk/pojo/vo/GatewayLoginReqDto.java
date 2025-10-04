package com.tkfc.sdk.pojo.vo;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import com.tkfc.core.common.annotations.web.validation.NotBlankString;
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
public class GatewayLoginReqDto {

    @NotBlankString
    @BodyProperty(tag = "用户名")
    private String username;

    @NotBlankString
    @BodyProperty(tag = "密码")
    private String password;

    /*设备码*/
    @NotBlankString
    @BodyProperty(tag = "设备码")
    private String deviceToken;

}
