package com.tkfc.sdk.google.translate.config;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleConfig {

    /**
     * 密钥
     */
    private String key;

    /**
     * 请求url链接
     */
    private String url;

}
