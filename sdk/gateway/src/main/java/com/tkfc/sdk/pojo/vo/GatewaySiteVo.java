package com.tkfc.sdk.pojo.vo;

import com.tkfc.boot.starter.mybatis.extend.BaseVo;
import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import com.tkfc.sdk.model.GatewaySite;
import lombok.*;

import java.util.List;


/**
 * 站点配置 vo
 *
 * @author 0neBean
 * @since 2023-10-29 12:24:54
 */
@Body(tag = "站点配置")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GatewaySiteVo extends BaseVo<GatewaySite> {

    /**
     * 站点名称
     */
    @BodyProperty(tag = "站点名称")
    private String siteName;

    /**
     * 站点KEY
     */
    @BodyProperty(tag = "站点KEY")
    private String siteKey;

    /**
     * 域名列表
     */
    @BodyProperty(tag = "域名列表")
    private List<GatewaySiteDomainVo> domains;

}
