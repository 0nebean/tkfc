package com.tkfc.sdk.pojo.dto;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import com.tkfc.core.common.annotations.web.validation.NotBlankString;
import com.tkfc.core.common.annotations.web.validation.NotNullValue;
import lombok.*;

/**
 * 选择租户请求参数
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
@Body(tag = "选择租户请求参数")
public class GatewaySelectTenantReqDto {

    @NotBlankString
    @BodyProperty(tag = "身份令牌")
    private String identityToken;

    @NotNullValue
    @BodyProperty(tag = "租户ID")
    private Long tenantId;
}
