package com.tkfc.sdk.pojo.vo;

import com.tkfc.boot.starter.mybatis.extend.BaseVo;
import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import com.tkfc.sdk.model.GatewayTenantUserBind;
import lombok.*;


/**
 * 平台租户用户关联关系表 vo
 *
 * @author 0neBean
 * @since 2023-03-24 20:28:02
 */
@Body(tag = "平台租户用户关联关系表")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GatewayTenantUserBindVo extends BaseVo<GatewayTenantUserBind> {

    /**
     * 凭证ID
     */
    @BodyProperty(tag = "凭证ID")
    private String ticketId;
    /**
     * 平台租户ID
     */
    @BodyProperty(tag = "平台租户ID")
    private Long tenantId;
    /**
     * 平台用户ID
     */
    @BodyProperty(tag = "平台用户ID")
    private Long userId;


}
