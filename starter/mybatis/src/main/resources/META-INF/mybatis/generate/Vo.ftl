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
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.fastjson2.JSONObject;
            </#if>
            <#if item.columnType == 'JSONArray'>
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.fastjson2.JSONArray;

            </#if>
        </#if>
    </#list>
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
    @BodyProperty(tag = "${item.comment}")
    private ${item.columnType} ${item.columnName};
        </#if>
    </#list>
</#if>


}
