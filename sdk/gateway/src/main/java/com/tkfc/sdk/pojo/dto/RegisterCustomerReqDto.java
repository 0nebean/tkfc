package com.tkfc.sdk.pojo.dto;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import com.tkfc.core.common.annotations.web.validation.NotBlankString;
import com.tkfc.core.common.annotations.web.validation.NotEmptyCollection;
import lombok.*;

import java.io.Serializable;
import java.util.List;


/**
 * 注册客户账号请求参数
 *
 * @author 0neBean
 * @since 2023-04-09 18:32:44
 */
@Body(tag = "注册客户账号请求参数")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterCustomerReqDto implements Serializable {


    private static final long serialVersionUID = 3507089006289256793L;

    @NotBlankString
    @BodyProperty(tag = "用户名")
    private String username;

    @NotBlankString
    @BodyProperty(tag = "手机号")
    private String mobile;

    @NotBlankString
    @BodyProperty(tag = "邮箱")
    private String email;

    @NotBlankString
    @BodyProperty(tag = "真实姓名")
    private String realName;

    @NotBlankString
    @BodyProperty(tag = "密码")
    private String password;

    @NotBlankString
    @BodyProperty(tag = "短信验证码")
    private String smsCode;

    @NotEmptyCollection
    @BodyProperty(tag = "感兴趣的应用类型")
    private List<String> interestedApp;

}
