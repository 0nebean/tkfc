package com.tkfc.sdk.model;

import com.tkfc.boot.starter.mybatis.extend.BaseModel;
import com.tkfc.core.common.annotations.orm.FiledName;
import com.tkfc.core.common.annotations.orm.TableName;
import lombok.*;


/**
 * 平台租户信息 model
 *
 * @author 0neBean
 * @since 2023-03-24 20:55:14
 */
@TableName("gateway_tenant")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GatewayTenant extends BaseModel {


    /**
     * 租户名称
     */
    @FiledName("tenant_name")
    private String tenantName;
    /**
     * 是否停用
     */
    @FiledName("is_lock")
    private String isLock;


}