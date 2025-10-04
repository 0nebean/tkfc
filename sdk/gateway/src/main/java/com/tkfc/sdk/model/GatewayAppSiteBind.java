package com.tkfc.sdk.model;

import com.tkfc.boot.starter.mybatis.extend.BaseModel;
import com.tkfc.core.common.annotations.orm.FiledName;
import com.tkfc.core.common.annotations.orm.TableName;
import lombok.*;


/**
 * 应用站点绑定表 model
 *
 * @author 0neBean
 * @since 2023-11-03 13:13:49
 */
@TableName("gateway_app_site_bind")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GatewayAppSiteBind extends BaseModel {


    /**
     * 应用ID
     */
    @FiledName("app_id")
    private Long appId;
    /**
     * 站点ID
     */
    @FiledName("site_id")
    private Long siteId;


}
