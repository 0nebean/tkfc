package com.tkfc.sdk.pojo.dto;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import com.tkfc.core.common.annotations.web.validation.NotBlankString;
import lombok.*;

import java.io.Serializable;


/**
 * 注册客户账号-发送短信验证码参数
 *
 * @author 0neBean
 * @since 2023-04-09 18:32:44
 */
@Body(tag = "注册客户账号-发送短信验证码参数")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendRegisterCustomerSmsReqDto implements Serializable {

    private static final long serialVersionUID = -5277878026442421902L;

    @NotBlankString
    @BodyProperty(tag = "手机号")
    private String mobile;

}
