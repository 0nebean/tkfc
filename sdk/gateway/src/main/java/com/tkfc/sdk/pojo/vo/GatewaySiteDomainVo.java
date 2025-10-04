package com.tkfc.sdk.pojo.vo;

import com.tkfc.boot.starter.mybatis.extend.BaseVo;
import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import com.tkfc.sdk.model.GatewaySiteDomain;
import lombok.*;


/**
 * 站点域名 vo
 *
 * @author 0neBean
 * @since 2023-10-29 12:24:53
 */
@Body(tag = "站点域名")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GatewaySiteDomainVo extends BaseVo<GatewaySiteDomain> {

    /**
     * 域名
     */
    @BodyProperty(tag = "域名")
    private String hostName;
    /**
     * 证书秘钥路径
     */
    @BodyProperty(tag = "证书秘钥路径")
    private String sslCrtKeyPath;
    /**
     * 证书路径
     */
    @BodyProperty(tag = "证书路径")
    private String sslCrtPath;

    /**
     * 代理端口号
     */
    @BodyProperty(tag = "代理端口号")
    private String proxyPort;
    /**
     * 是否开启ssl
     */
    @BodyProperty(tag = "是否开启ssl")
    private String isSsl;
    /**
     * ssl代理端口号
     */
    @BodyProperty(tag = "ssl代理端口号")
    private String sslProxyPort;
    /**
     * 站点ID
     */
    @BodyProperty(tag = "站点ID")
    private Long siteId;
    /**
     * 站点KEY
     */
    @BodyProperty(tag = "站点KEY")
    private String siteKey;

}
