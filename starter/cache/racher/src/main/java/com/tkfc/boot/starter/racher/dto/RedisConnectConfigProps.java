package com.tkfc.boot.starter.racher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * redis 连接配置类
 *
 * @author 0neBean
 * @since 2022-09-08 23:17:41
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RedisConnectConfigProps {

    private String useModel;
    private String setMasterName;
    private String setReadMode;
    private String address;
    private String hostName;
    private String password;
    private int port;
    private int setTimeout;
    private int database;

}
