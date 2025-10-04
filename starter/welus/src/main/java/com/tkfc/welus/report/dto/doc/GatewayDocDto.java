package com.tkfc.welus.report.dto.doc;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import lombok.*;

import java.util.List;


/**
 * 文档管理
 *
 * @author 0neBean
 * @since 2022-06-17 22:59:44
 */
@Getter
@Setter
@Builder
@Body(tag = "文档信息")
@AllArgsConstructor
@NoArgsConstructor
public class GatewayDocDto {

    @BodyProperty(tag = "是否是固定的path")
    private String absolutePath;

    @BodyProperty(tag = "action资源定义key")
    private String actionDefKey;

    @BodyProperty(tag = "唯一键 UUID")
    private String resourceDefKey;

    @BodyProperty(tag = "应用标识")
    private String appKey;

    @BodyProperty(tag = "资源类型 (0:action ,1:api)")
    private String resourceType;

    @BodyProperty(tag = "资源名称")
    private String resourceName;

    @BodyProperty(tag = "资源地址")
    private String resourcePath;

    @BodyProperty(tag = "接口类型")
    private String methodType;

    @BodyProperty(tag = "权限类型 (0:free ,1:spring)")
    private String authType;

    @BodyProperty(tag = "作者信息")
    private String authors;

    @BodyProperty(tag = "参数")
    private List<GatewayDocParamDto> params;

    @BodyProperty(tag = "返回值")
    private GatewayDocParamDto returnResult;

}
