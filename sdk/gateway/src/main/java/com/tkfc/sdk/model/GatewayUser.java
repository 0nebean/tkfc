package com.tkfc.sdk.model;

import com.tkfc.boot.starter.mybatis.extend.BaseModel;
import com.tkfc.core.common.annotations.orm.FiledName;
import com.tkfc.core.common.annotations.orm.TableName;
import lombok.*;


/**
 * 网关用户 model
 *
 * @author 0neBean
 * @since 2024-06-03 18:11:07
 */
@TableName("gateway_user_")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GatewayUser extends BaseModel {


    /**
     * 租户ID
     */
    @FiledName("tenant_id")
    private String tenantId;
    /**
     * 邮件
     */
    @FiledName("email")
    private String email;
    /**
     * 手机号
     */
    @FiledName("mobile_number")
    private String mobileNumber;
    /**
     * 意向产品ids
     */
    @FiledName("interested_app")
    private String interestedApp;
    /**
     * 是否锁定 (0:否 ,1:是)
     */
    @FiledName("is_active")
    private Boolean isActive;
    /**
     * 密码
     */
    @FiledName("password")
    private String password;
    /**
     * 真实姓名
     */
    @FiledName("real_name")
    private String realName;
    /**
     * 用户名
     */
    @FiledName("username")
    private String username;


}