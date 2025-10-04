package com.tkfc.sdk.pojo.dto;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import com.tkfc.core.common.annotations.web.validation.NotBlankString;
import lombok.*;

/**
 * 校验验证码请求参数
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
@Body(tag = "校验验证码请求参数")
public class GatewaySmsCodeCheckReqDto {

    @NotBlankString
    @BodyProperty(tag = "用户名")
    private String smsCode;
    @NotBlankString
    @BodyProperty(tag = "身份令牌")
    private String identityToken;
}
