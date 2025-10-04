package com.tkfc.welus.report.dto.doc;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import lombok.*;

import java.util.List;


/**
 * 文档参数信息
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
public class GatewayDocParamDto {

    @BodyProperty(tag = "应用key")
    private String appKey;

    @BodyProperty(tag = "所属方法唯一标识")
    private String methodDefKey;

    @BodyProperty(tag = "类型定义key")
    private String typeDefKey;

    @BodyProperty(tag = "类型完全限定名")
    private String typeFullName;

    @BodyProperty(tag = "所属body对象定义key")
    private String fieldDefKey;

    @BodyProperty(tag = "父级定义key")
    private String parentDefKey;

    @BodyProperty(tag = "字段类型 0: 参数, 1:返回值")
    private String fieldType;

    @BodyProperty(tag = "参数类型 (0:pathParam , 1:urlParam , 2:bodyObject , 3:bodyField)")
    private String paramType;

    @BodyProperty(tag = "参数数据名称")
    private String paramName;

    @BodyProperty(tag = "参数注释")
    private String paramDesc;

    @BodyProperty(tag = "参数示例")
    private String paramExample;

    @BodyProperty(tag = "包装类型")
    private String covertType;

    @BodyProperty(tag = "排序")
    private Integer sort;

    @BodyProperty(tag = "body字段")
    private List<GatewayDocParamDto> bodyFieldList;
}
