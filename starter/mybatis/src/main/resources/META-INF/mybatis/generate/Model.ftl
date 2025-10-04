package ${modelPackageName};

import com.tkfc.core.common.annotations.orm.FiledName;
import com.tkfc.core.common.annotations.orm.TableName;
import com.tkfc.boot.starter.mybatis.extend.BaseModel;
import lombok.*;

<#if fieldArr?exists>
    <#list fieldArr as item>
        <#if item.columnName != 'id' && item.columnName != 'createTime' && item.columnName != 'updateTime' &&
        item.columnName != 'operatorId' && item.columnName != 'operatorName' && item.columnName != 'isDeleted'>
            <#if item.columnType == 'BigDecimal'>
import java.math.BigDecimal;
            </#if>
            <#if item.columnType == 'LocalDateTime'>
import java.time.LocalDateTime;
            </#if>
            <#if item.columnType == 'JSONObject'>
import com.alibaba.fastjson2.JSONObject;
            </#if>
            <#if item.columnType == 'JSONArray'>
import com.alibaba.fastjson2.JSONArray;
            </#if>
        </#if>
    </#list>
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
