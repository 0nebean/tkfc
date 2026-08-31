package com.tkfc.sdk.tencent.cos.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 腾讯云 COS 配置
 *
 * @author 0neBean
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CosConfig {

    /**
     * 地域，例如 ap-shanghai
     */
    private String region;

    /**
     * SecretId
     */
    private String secretId;

    /**
     * SecretKey
     */
    private String secretKey;
}
