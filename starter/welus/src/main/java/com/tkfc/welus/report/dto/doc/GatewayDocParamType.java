package com.tkfc.welus.report.dto.doc;

import com.alibaba.fastjson2.JSONObject;
import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import lombok.*;


/**
 * 文档参数类型
 *
 * @author 0neBean
 * @since 2022-06-17 15:53:56
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Body(tag = "文档参数信息")
public class GatewayDocParamType {

    @BodyProperty(tag = "应用标识")
    private String appKey;

    @BodyProperty(tag = "类型定义key")
    private String typeDefKey;

    @BodyProperty(tag = "简单类型")
    private String simpleType;

    @BodyProperty(tag = "完整类型")
    private String fullType;

    @BodyProperty(tag = "mock数据json")
    private String mockData;

}
