package com.tkfc.sdk.model;

import com.tkfc.boot.starter.mybatis.extend.BaseModel;
import com.tkfc.core.common.annotations.orm.FiledName;
import com.tkfc.core.common.annotations.orm.TableName;
import lombok.*;


/**
 * 站点配置 model
 *
 * @author 0neBean
 * @since 2023-10-29 12:24:54
 */
@TableName("gateway_site")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GatewaySite extends BaseModel {


    /**
     * 站点名称
     */
    @FiledName("site_name")
    private String siteName;


    /**
     * 站点名称
     */
    @FiledName("site_key")
    private String siteKey;


}
