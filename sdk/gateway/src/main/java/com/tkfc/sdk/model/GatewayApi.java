package com.tkfc.sdk.model;

import com.tkfc.boot.starter.mybatis.extend.BaseModel;
import com.tkfc.core.common.annotations.orm.FiledName;
import com.tkfc.core.common.annotations.orm.TableName;
import lombok.*;


/**
 * 接口信息 model
 *
 * @author 0neBean
 * @since 2022-07-19 23:38:25
 */
@TableName("gateway_api")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GatewayApi extends BaseModel {


    /**
     * 应用标识
     */
    @FiledName("app_key")
    private String appKey;
    /**
     * 接口名称
     */
    @FiledName("api_name")
    private String apiName;
    /**
     * 代理访问地址
     */
    @FiledName("proxy_path")
    private String proxyPath;
    /**
     * 真实接口地址
     */
    @FiledName("api_uri")
    private String apiUri;
    /**
     * 是否停用 0:否 1:是
     */
    @FiledName("is_lock")
    private String isLock;

}