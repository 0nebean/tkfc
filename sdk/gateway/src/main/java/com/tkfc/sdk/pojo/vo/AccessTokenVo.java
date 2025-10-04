package com.tkfc.sdk.pojo.vo;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import lombok.*;

/**
 * oauth授权码vo对象
 *
 * @author 0neBean
 * @since 2023-03-24 01:20:09
 */
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Body(tag = "访问令牌响应参数")
public class AccessTokenVo {

    /*凭证ID*/
    @BodyProperty(tag = "凭证ID")
    private String ticketId;
    /*授权码*/
    @BodyProperty(tag = "授权码")
    private String accessToken;
    /*授权码有效时间*/
    @BodyProperty(tag = "授权码有效时间")
    private Long accessTokenExpireTime;
    /*设备码*/
    @BodyProperty(tag = "设备码")
    private String deviceToken;

}
