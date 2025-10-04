package com.tkfc.welus.pojo;

import com.alibaba.fastjson2.JSONObject;
import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 接口访问日志
 *
 * @author 0neBean
 * @version 1.0
 * @since 2024-12-17 16:59:09
 */
@Getter
@Setter
@Builder
@ToString
@Body(tag = "网关订阅消息")
public class AccessLog {

    @BodyProperty(tag = "http方法类型")
    private String httpMethodName;
    @BodyProperty(tag = "路径")
    private String path;
    @BodyProperty(tag = "参数")
    private Object parameter;
    @BodyProperty(tag = "响应")
    private Object responseBody;
    @BodyProperty(tag = "请求头")
    private JSONObject  headers;
    @BodyProperty(tag = "方法名")
    private String methodName;
    @BodyProperty(tag = "访问IP")
    private String accessIp;
    @BodyProperty(tag = "请求时间")
    private String accessDateStr;
    @BodyProperty(tag = "执行耗时")
    private String executionTimeStr;


}
