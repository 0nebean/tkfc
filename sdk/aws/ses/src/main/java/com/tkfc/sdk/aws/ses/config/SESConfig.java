package com.tkfc.sdk.aws.ses.config;

import lombok.*;
import software.amazon.awssdk.regions.Region;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SESConfig {

    /**
     * ak密钥
     */
    private String accessKey;

    /**
     * sk密钥
     */
    private String secretKey;

    /**
     * 地区编码
     */
    private Region region;
}
