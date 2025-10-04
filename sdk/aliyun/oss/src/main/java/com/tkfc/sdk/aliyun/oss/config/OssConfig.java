package com.tkfc.sdk.aliyun.oss.config;

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
public class OssConfig {

    private String endpoint;

    private String accessId;

    private String accessKey;

}