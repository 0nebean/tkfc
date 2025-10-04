package com.tkfc.sdk.pojo.vo;

import com.tkfc.boot.starter.mybatis.extend.BaseVo;
import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import com.tkfc.sdk.model.GatewaySiteDomainBind;
import lombok.*;


/**
 * 站点绑定域名 vo
 *
 * @author 0neBean
 * @since 2023-10-29 12:20:36
 */
@Body(tag = "站点绑定域名")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GatewaySiteDomainBindVo extends BaseVo<GatewaySiteDomainBind> {

    /**
     * 站点ID
     */
    @BodyProperty(tag = "站点ID")
    private Long siteId;
    /**
     * 域名ID
     */
    @BodyProperty(tag = "域名ID")
    private Long domainId;


}
