package com.tkfc.sdk.pojo.vo;

import com.tkfc.boot.starter.mybatis.extend.BaseVo;
import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import com.tkfc.sdk.model.GatewayUser;
import lombok.*;


/**
 * 网关用户 vo
 *
 * @author 0neBean
 * @since 2024-06-03 18:11:07
 */
@Body(tag = "网关用户")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GatewayUserVo extends BaseVo<GatewayUser> {

    /**
     * 租户ID
     */
    @BodyProperty(tag = "租户ID")
    private String tenantId;
    /**
     * 邮件
     */
    @BodyProperty(tag = "邮件")
    private String email;
    /**
     * 手机号
     */
    @BodyProperty(tag = "手机号")
    private String mobileNumber;
    /**
     * 意向产品ids
     */
    @BodyProperty(tag = "意向产品ids")
    private String interestedApp;
    /**
     * 是否启用 (0:否 ,1:是)
     */
    @BodyProperty(tag = "是否启用 (0:否 ,1:是)")
    private Boolean isActive;
    /**
     * 密码
     */
    @BodyProperty(tag = "密码")
    private String password;
    /**
     * 真实姓名
     */
    @BodyProperty(tag = "真实姓名")
    private String realName;
    /**
     * 用户名
     */
    @BodyProperty(tag = "用户名")
    private String username;


}
