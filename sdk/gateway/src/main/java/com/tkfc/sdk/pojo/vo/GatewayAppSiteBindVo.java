package com.tkfc.sdk.pojo.vo;

import com.tkfc.boot.starter.mybatis.extend.BaseVo;
import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import com.tkfc.sdk.model.GatewayAppSiteBind;
import lombok.*;


/**
 * 应用站点绑定表 vo
 *
 * @author 0neBean
 * @since 2023-11-03 13:13:49
 */
@Body(tag = "应用站点绑定表")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GatewayAppSiteBindVo extends BaseVo<GatewayAppSiteBind> {

    /**
     * 应用ID
     */
    @BodyProperty(tag = "应用ID")
    private Long appId;
    /**
     * 站点ID
     */
    @BodyProperty(tag = "站点ID")
    private Long siteId;


}
