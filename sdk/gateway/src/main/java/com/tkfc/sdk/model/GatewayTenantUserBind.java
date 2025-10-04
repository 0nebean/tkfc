package com.tkfc.sdk.model;

import com.tkfc.boot.starter.mybatis.extend.BaseModel;
import com.tkfc.core.common.annotations.orm.FiledName;
import com.tkfc.core.common.annotations.orm.TableName;
import lombok.*;


/**
 * 平台租户用户关联关系表 model
 *
 * @author 0neBean
 * @since 2023-03-24 20:28:02
 */
@TableName("gateway_tenant_user_bind")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GatewayTenantUserBind extends BaseModel {


    /**
     * 平台租户ID
     */
    @FiledName("ticket_id")
    private String ticketId;
    /**
     * 平台租户ID
     */
    @FiledName("tenant_id")
    private Long tenantId;
    /**
     * 平台用户ID
     */
    @FiledName("user_id")
    private Long userId;


}