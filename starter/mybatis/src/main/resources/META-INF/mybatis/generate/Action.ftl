package ${actionPackageName};

import com.tkfc.core.common.annotations.web.action.RestAction;
import com.tkfc.core.common.annotations.web.auth.Authenticated;
import com.tkfc.core.common.annotations.web.method.json.PostJson;
import com.tkfc.core.common.annotations.web.param.BodyParam;
import com.tkfc.core.common.annotations.web.param.UrlParam;
import ${servicePackageName}.${modelName}Service;
import ${voPackageName}.${modelName}Vo;
import lombok.AllArgsConstructor;
<#if isSplitTable == true>
import com.tkfc.sdk.utils.GatewayAuthUtil;
</#if>
import com.tkfc.boot.starter.mybatis.sql.build.SqlBuilder;
import com.tkfc.boot.starter.mybatis.sql.wrapper.SqlWrapper;
import com.tkfc.core.common.pojo.BasePageExpressionRequest;
import com.tkfc.core.common.pojo.BaseResponse;
import com.tkfc.core.toolkit.ExcelUtil;
import ${modelPackageName}.${modelName};
<#if geneDataType == "tree">
import ${voPackageName}.${modelName}Tree;
import com.tkfc.core.common.pojo.BaseTreeDataDto
import lombok.NonNull;
import java.util.HashSet;
import java.util.Optional;
<#else>

</#if>
import java.io.IOException;
import java.util.List;

/**
 * ${description} action
 *
 * @author ${author}
 * @version 1.0
 * @since 2024-06-07 15:15:41
 */
@AllArgsConstructor
@RestAction(tag = "系统${description}管理", path = "${modelVarName}")
public class ${modelName}Action {

    private final ${modelName}Service ${modelVarName}Service;
    private final static String[] ALLOW_FIELDS = {"id"};

    @Authenticated("${permShortName}_VIEW")
    @PostJson(authors = {"${author}"}, tag = "${description}详情", path = {"detail"})
    public ${modelName}Vo detail(@UrlParam(tag = "主键") Long id) {
        return ${modelVarName}Service.findById(id<#if isSplitTable == true>, GatewayAuthUtil.getTenantId()</#if>).toVo(${modelName}Vo.class);
    }

    @Authenticated("${permShortName}_SAVE")
    @PostJson(authors = {"${author}"}, tag = "保存${description}", path = {"save"})
    public Integer save(@BodyParam(tag = "实体类vo") ${modelName}Vo entity) {
        return ${modelVarName}Service.save(entity.toModel()<#if isSplitTable == true>, GatewayAuthUtil.getTenantId()</#if>);
    }

    @Authenticated("${permShortName}_DEL")
    @PostJson(authors = {"${author}"}, tag = "删除${description}", path = {"delete"})
    public Integer delete(@BodyParam(tag = "删除入参") BasePageExpressionRequest request) {
        return ${modelVarName}Service.deleteByIds(request.getSelectedIds()<#if isSplitTable == true>, GatewayAuthUtil.getTenantId()</#if>);
    }

    @Authenticated("${permShortName}_PAGE")
    @PostJson(authors = {"0neBean"}, tag = "${description}数据分页", path = {"page"})
    public BaseResponse<List<${modelName}Vo>> page(@BodyParam(tag = "通用列表请求对象") BasePageExpressionRequest request) {
        SqlWrapper<${modelName}> sql = SqlBuilder.<${modelName}>init().expression(request, ALLOW_FIELDS);
        return BaseResponse.ok(${modelVarName}Service.toVos(${modelVarName}Service.find(sql<#if isSplitTable == true>, GatewayAuthUtil.getTenantId()</#if>)), sql.getPagination());
    }

    @Authenticated("${permShortName}_EXPORT")
    @PostJson(authors = {"0neBean"}, tag = "导出Excel", path = {"export"})
    public String export(@BodyParam(tag = "通用列表请求对象") BasePageExpressionRequest request) throws IOException {
        BaseResponse<List<${modelName}Vo>> result = ${modelVarName}Service.findPage(request,<#if isSplitTable == true>GatewayAuthUtil.getTenantId(),</#if> ALLOW_FIELDS);
        return ExcelUtil.writeExcelFileOnResp(result.getData(), ${modelName}Vo.class);
    }

<#if geneDataType == "tree">
    @Authenticated("${permShortName}_PAGE")
    @PostJson(authors = {"${author}"}, tag = "同步${description}树", path = {"syncTreeData"})
    public List<${modelName}Tree> syncTreeData(@BodyParam(tag = "加载树的请求参数") @NonNull BaseTreeDataDto req) {
        Long selfId = Optional.of(req).map(BaseTreeDataDto::getSelfId).orElse(null);
        return ${modelVarName}Service.findChildSync(<#if isSplitTable == true>GatewayAuthUtil.getTenantId(), </#if>selfId, new HashSet<>(), req.getExpressions(), ALLOW_FIELDS);
    }

    @Authenticated("${permShortName}_PAGE")
    @PostJson(authors = {"${author}"}, tag = "异步${description}树", path = {"asyncTreeData"})
    public List<${modelName}Tree> asyncTreeData(@BodyParam(tag = "加载树的请求参数") @NonNull BaseTreeDataDto req) {
        Long parentId = Optional.of(req).map(BaseTreeDataDto::getParentId).orElse(null);
        return ${modelVarName}Service.findChildAsync(<#if isSplitTable == true>GatewayAuthUtil.getTenantId(), </#if>parentId, new HashSet<>(), req.getExpressions(), ALLOW_FIELDS);
    }
<#else>

</#if>

}