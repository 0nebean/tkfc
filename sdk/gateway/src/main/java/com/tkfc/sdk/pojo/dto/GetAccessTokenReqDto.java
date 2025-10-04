package com.tkfc.sdk.pojo.dto;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import com.tkfc.core.common.annotations.web.validation.NotBlankString;
import lombok.*;

/**
 * 获取访问令牌请求参数
 *
 * @author 0neBean
 * @since 2023-03-23 22:22:36
 */
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Body(tag = "获取访问令牌请求参数")
public class GetAccessTokenReqDto {

    /*凭证ID*/
    @NotBlankString
    @BodyProperty(tag = "凭证ID")
    private String ticketId;
    /*时间戳*/
    @NotBlankString
    @BodyProperty(tag = "时间戳")
    private String timestamp;
    /*签名*/
    @NotBlankString
    @BodyProperty(tag = "签名")
    private String sign;
    /*设备码*/
    @BodyProperty(tag = "设备码")
    private String deviceToken;

}
