package com.tkfc.sdk.model;

import com.tkfc.boot.starter.mybatis.extend.BaseModel;
import com.tkfc.core.common.annotations.orm.FiledName;
import com.tkfc.core.common.annotations.orm.TableName;
import lombok.*;


/**
 * 站点域名 model
 *
 * @author 0neBean
 * @since 2023-10-29 12:24:53
 */
@TableName("gateway_site_domain")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GatewaySiteDomain extends BaseModel {


    /**
     * 域名
     */
    @FiledName("host_name")
    private String hostName;
    /**
     * 证书秘钥路径
     */
    @FiledName("ssl_crt_key_path")
    private String sslCrtKeyPath;
    /**
     * 证书路径
     */
    @FiledName("ssl_crt_path")
    private String sslCrtPath;
    /**
     * 代理端口号
     */
    @FiledName("proxy_port")
    private String proxyPort;
    /**
     * 是否开启ssl
     */
    @FiledName("is_ssl")
    private String isSsl;
    /**
     * ssl代理端口号
     */
    @FiledName("ssl_proxy_port")
    private String sslProxyPort;

}
