package com.tkfc.welus.report.dto.doc;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import lombok.*;

import java.util.List;
import java.util.Map;


/**
 * 文档上报对象
 *
 * @author 0neBean
 * @since 2022-06-17 22:59:44
 */
@Getter
@Setter
@Builder
@Body(tag = "文档上报参数")
@AllArgsConstructor
@NoArgsConstructor
public class GatewayDocReportDto {

    @BodyProperty(tag = "文档资源信息")
    private List<GatewayDocDto> resources;

    @BodyProperty(tag = "文档参数信息")
    private List<GatewayDocParamDto> params;

    @BodyProperty(tag = "参数类型")
    private Map<String, GatewayDocParamType> types;

    @BodyProperty(tag = "应用标识")
    private String appKey;

}
