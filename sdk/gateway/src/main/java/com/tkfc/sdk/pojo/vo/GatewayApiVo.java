package com.tkfc.sdk.pojo.vo;

import com.tkfc.boot.starter.mybatis.extend.BaseVo;
import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import com.tkfc.core.common.annotations.web.response.Dictionary;
import com.tkfc.sdk.model.GatewayApi;
import lombok.*;


/**
 * 接口信息 vo
 *
 * @author 0neBean
 * @since 2022-07-19 23:38:25
 */
@Body(tag = "接口信息")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GatewayApiVo extends BaseVo<GatewayApi> {

    /**
     * 应用标识
     */
    @BodyProperty(tag = "应用标识")
    private String appKey;
    /**
     * 接口名称
     */
    @BodyProperty(tag = "接口名称")
    private String apiName;
    /**
     * 代理访问地址
     */
    @BodyProperty(tag = "代理访问地址")
    private String proxyPath;
    /**
     * 真实接口地址
     */
    @BodyProperty(tag = "真实接口地址")
    private String apiUri;
    /**
     * 是否停用 0:否 1:是
     */
    @Dictionary(groupVal = "SF")
    @BodyProperty(tag = "是否停用 0:否 1:是")
    private String isLock;
    /**
     * 绑定ID
     */
    @BodyProperty(tag = "绑定ID")
    private Long bindId;
    /**
     * 是否需要登录
     */
    @BodyProperty(tag = "是否需要登录")
    private String needLogin;

    /**
     * 一小时内调用频次
     */
    @BodyProperty(tag = "一小时内调用频次")
    private Integer callTimesPreHour;

    /**
     * 当前一小时内调用频次
     */
    @BodyProperty(tag = "当前一小时内调用频次")
    private Integer callTimesCurrentHour;

    /**
     * 调用的小时编码
     */
    @BodyProperty(tag = "调用的小时编码")
    private String callTimesPressHour;
}