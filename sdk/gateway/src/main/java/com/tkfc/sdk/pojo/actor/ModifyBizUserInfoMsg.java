package com.tkfc.sdk.pojo.actor;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import com.tkfc.sdk.pojo.actor.enums.ModifyUserTypeEnum;
import lombok.*;

import java.util.List;

/**
 * 编辑用户信息消息
 *
 * @author 0neBean
 * @version 1.0
 * @since 2024-08-13 19:15:32
 */
@Body(tag = "编辑用户信息消息")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModifyBizUserInfoMsg {

    @BodyProperty(tag = "用户ID")
    private Long userId;

    @BodyProperty(tag = "用户开放ID")
    private String userOpenId;

    @BodyProperty(tag = "用户名")
    private String username;

    @BodyProperty(tag = "密码")
    private String password;

    @BodyProperty(tag = "凭证ID")
    private String ticketId;

    @BodyProperty(tag = "是否删除")
    private String isDeleted;

    @BodyProperty(tag = "是否启用")
    private Boolean isActive;

    @BodyProperty(tag = "邮件")
    private String email;

    @BodyProperty(tag = "手机号")
    private String mobileNumber;

    @BodyProperty(tag = "真实姓名")
    private String realName;

    @BodyProperty(tag = "选中的ID")
    private List<Long> selectedIds;

    @BodyProperty(tag = "编辑类型")
    private ModifyUserTypeEnum modifyType;


}
