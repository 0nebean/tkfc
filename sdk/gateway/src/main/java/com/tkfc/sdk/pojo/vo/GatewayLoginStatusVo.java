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
@Body(tag = "网关用户登录信息")
public class GatewayLoginStatusVo {

    @BodyProperty(tag = "用户ID")
    private String userId;

    @BodyProperty(tag = "邮箱")
    private String email;

    @BodyProperty(tag = "手机号")
    private String mobile;

    @BodyProperty(tag = "是否登录")
    private Boolean isLogin;

}
