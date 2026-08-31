package ${modelPackageName};

import com.tkfc.core.common.annotations.orm.FiledName;
import com.tkfc.core.common.annotations.orm.TableName;
import com.tkfc.boot.starter.mybatis.extend.BaseModel;
import lombok.*;
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
<#if fieldArr?exists>
    <#list fieldArr as item>
        <#if item.isEnum>
import ${item.enumPackageName}.${item.enumClassName};
        </#if>
    </#list>
</#if>
<#if hasBigDecimal>
import java.math.BigDecimal;
</#if>
<#if hasLocalDateTime>
import java.time.LocalDateTime;
</#if>
<#if hasJSONObject>
import com.alibaba.fastjson2.JSONObject;
</#if>
<#if hasJSONArray>
import com.alibaba.fastjson2.JSONArray;
</#if>

/**
 * ${description} model
 *
 * @author ${author}
 * @since ${createTime}
 */
@TableName("${tableName}")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ${modelName} extends BaseModel {


<#if fieldArr?exists>
    <#list fieldArr as item>
        <#if item.columnName != 'id' && item.columnName != 'createTime' && item.columnName != 'updateTime' &&
        item.columnName != 'operatorId' && item.columnName != 'operatorName' && item.columnName != 'isDeleted'>
    /**
     * ${item.comment}
     */
    @FiledName("${item.originalName}")
    private ${item.columnType} ${item.columnName};
        </#if>
    </#list>
</#if>


}
