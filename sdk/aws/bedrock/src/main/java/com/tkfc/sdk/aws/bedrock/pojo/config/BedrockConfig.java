package com.tkfc.sdk.aws.bedrock.pojo.config;

import lombok.*;

/**
 * s3 配置实例
 *
 * @author 0neBean
 * @since 2022-08-09 11:13:14
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BedrockConfig {

    private String accessKey;

    private String secretKey;

    private String region;

    /**
     * 连接超时时间 单位秒
     */
    private Long connectionTimeout;

    /**
     * 请求超时时间 单位秒
     */
    private Long socketTimeout;

    /**
     * 代理地址
     */
    private String proxyEndpoint;

}