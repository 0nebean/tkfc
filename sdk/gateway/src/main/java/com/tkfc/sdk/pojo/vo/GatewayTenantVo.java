package com.tkfc.sdk.pojo.vo;

import com.tkfc.boot.starter.mybatis.extend.BaseVo;
import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import com.tkfc.core.common.annotations.web.response.Dictionary;
import com.tkfc.sdk.model.GatewayTenant;
import lombok.*;


/**
 * 平台租户信息 vo
 *
 * @author 0neBean
 * @since 2023-03-24 20:55:14
 */
@Body(tag = "平台租户信息")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GatewayTenantVo extends BaseVo<GatewayTenant> {

    /**
     * 租户名称
     */
    @BodyProperty(tag = "租户名称")
    private String tenantName;
    /**
     * 是否停用
     */
    @Dictionary(groupVal = "SF")
    @BodyProperty(tag = "是否停用")
    private String isLock;


}
