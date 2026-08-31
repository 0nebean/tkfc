package ${voPackageName};

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import com.tkfc.boot.starter.mybatis.extend.BaseVo;
import ${modelPackageName}.${modelName};
import lombok.*;
<#assign hasEnumField = false />
<#if fieldArr?exists>
    <#list fieldArr as item>
        <#if item.isEnum>
            <#assign hasEnumField = true />
        </#if>
    </#list>
</#if>
<#if hasEnumField>
import com.tkfc.core.common.annotations.web.response.Wrap;
import com.tkfc.core.common.annotations.web.response.Enum;
</#if>
<#if fieldArr?exists>
    <#list fieldArr as item>
        <#if item.isEnum>
import ${item.enumPackageName}.${item.enumClassName};
        </#if>
    </#list>
</#if>


<#assign hasBigDecimal = false />
<#assign hasLocalDateTime = false />
<#assign hasJSONObject = false />
<#assign hasJSONArray = false />
<#if fieldArr?exists>
    <#list fieldArr as item>
        <#if item.columnName != 'id' && item.columnName != 'createTime' && item.columnName != 'updateTime' &&
        item.columnName != 'operatorId' && item.columnName != 'operatorName' && item.columnName != 'isDeleted'>
            <#if item.columnType == 'BigDecimal'>
                <#assign hasBigDecimal = true />
            </#if>
            <#if item.columnType == 'LocalDateTime'>
                <#assign hasLocalDateTime = true />
            </#if>
            <#if item.columnType == 'JSONObject'>
                <#assign hasJSONObject = true />
            </#if>
            <#if item.columnType == 'JSONArray'>
                <#assign hasJSONArray = true />
            </#if>
        </#if>
    </#list>
</#if>
<#if hasBigDecimal>
import java.math.BigDecimal;
</#if>
<#if hasLocalDateTime>
import java.time.LocalDateTime;
</#if>
<#if hasJSONObject || hasJSONArray>
import com.alibaba.excel.annotation.ExcelIgnore;
</#if>
<#if hasJSONObject>
import com.alibaba.fastjson2.JSONObject;
</#if>
<#if hasJSONArray>
import com.alibaba.fastjson2.JSONArray;
</#if>

/**
 * ${description} vo
 *
 * @author ${author}
 * @since ${createTime}
 */
@Body(tag = "${description}")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ColumnWidth(25)
@HeadRowHeight(20)
@ContentRowHeight(18)
<#if hasEnumField>
@Wrap
</#if>
public class ${modelName}Vo extends BaseVo<${modelName}> {


<#if fieldArr?exists>
    <#list fieldArr as item>
        <#if item.columnName != 'createTime' && item.columnName != 'updateTime' &&
        item.columnName != 'operatorId' && item.columnName != 'operatorName' && item.columnName != 'isDeleted'>
    /**
     * ${item.comment}
     */
    <#if item.columnType == 'JSONObject' || item.columnType == 'JSONArray'>
    @ExcelIgnore
    <#else>
    @ExcelProperty(value = "${item.comment}")
    </#if>
    <#if item.isEnum>
    @Enum(using = ${item.enumClassName}.class)
    </#if>
    @BodyProperty(tag = "${item.comment}")
    <#if item.isEnum>
    private String ${item.columnName};
    <#else>
    private ${item.columnType} ${item.columnName};
    </#if>
        </#if>
    </#list>
</#if>


}
