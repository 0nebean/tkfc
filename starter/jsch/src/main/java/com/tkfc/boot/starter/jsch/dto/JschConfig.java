package com.tkfc.boot.starter.jsch.dto;

import lombok.*;

/**
 * 连接配置
 *
 * @author 0neBean
 * @since 2022-08-05 15:43:04
 */
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class JschConfig {

    private String host;
    private String rsaPath;
    private String user;
    private String password;
    private int port = 22;
    private String socks5ProxyHost;
    private int socks5ProxyPort;
    private String strictHostKeyChecking;
    private int timeout = 0;

}
