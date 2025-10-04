package ${servicePackageName};

<#if isSplitTable == true>
import com.tkfc.boot.starter.mybatis.extend.BaseSplitService;
<#else>
import com.tkfc.boot.starter.mybatis.extend.BaseService;
</#if>
import ${modelPackageName}.${modelName};
import ${voPackageName}.${modelName}Vo;
<#if geneDataType == "tree">
import ${voPackageName}.${modelName}Tree;

import java.util.List;
import java.util.Set;
</#if>

/**
 * ${description} service
 *
 * @author ${author}
 * @since ${createTime}
 */
<#if isSplitTable == true>
public interface ${modelName}Service extends BaseSplitService<${modelName}, ${modelName}Vo> {
<#else>
public interface ${modelName}Service extends BaseService<${modelName}, ${modelName}Vo> {
</#if>

<#if geneDataType == "tree">
    /**
     * 异步查询树
     *
     *<#if isSplitTable == true>@param tenantId    租户ID</#if>,
     * @param parentId    父级ID
     * @param selectedIds 选中的ID
     * @param expressions 条件表达式
     * @param allowFields 允许查询的字段
     * @return 菜单树
     */
     List<${modelName}Tree> findChildAsync(<#if isSplitTable == true>String tenantId, </#if>Long parentId, Set<Long> selectedIds, String[] expressions, String[] allowFields);

    /**
     * 同步查询树
     *
     *<#if isSplitTable == true>@param tenantId    租户ID</#if>,
     * @param selfId      自身的ID
     * @param selectedIds 选中的ID
     * @param expressions 条件表达式
     * @param allowFields 允许查询的字段
     * @return 菜单树
     */
     List<${modelName}Tree> findChildSync(<#if isSplitTable == true>String tenantId, </#if>Long selfId, Set<Long> selectedIds, String[] expressions, String[] allowFields);
</#if>
}