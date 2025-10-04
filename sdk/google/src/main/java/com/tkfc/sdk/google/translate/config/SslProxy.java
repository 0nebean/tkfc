package com.tkfc.sdk.google.translate.config;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SslProxy {

    /**
     * 本地ID eg: 127.0.0.1
     */
    private String ip;

    /**
     * 端口号 eg: 1080
     */
    private Integer port;

}
