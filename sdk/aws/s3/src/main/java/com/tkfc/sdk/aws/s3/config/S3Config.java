package com.tkfc.sdk.aws.s3.config;

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
public class S3Config {

    private String accessKey;

    private String secretKey;

    private String region;

}