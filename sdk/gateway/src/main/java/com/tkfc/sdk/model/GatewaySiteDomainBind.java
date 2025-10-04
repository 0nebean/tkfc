package com.tkfc.sdk.model;

import com.tkfc.boot.starter.mybatis.extend.BaseModel;
import com.tkfc.core.common.annotations.orm.FiledName;
import com.tkfc.core.common.annotations.orm.TableName;
import lombok.*;


/**
 * 站点绑定域名 model
 *
 * @author 0neBean
 * @since 2023-10-29 12:20:36
 */
@TableName("gateway_site_domain_bind")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GatewaySiteDomainBind extends BaseModel {


    /**
     * 站点ID
     */
    @FiledName("site_id")
    private Long siteId;
    /**
     * 域名ID
     */
    @FiledName("domain_id")
    private Long domainId;


}
