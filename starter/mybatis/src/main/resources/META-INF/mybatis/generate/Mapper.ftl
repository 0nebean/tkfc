package ${daoPackageName};

<#if isSplitTable>
import com.tkfc.boot.starter.mybatis.extend.BaseSplitMapper;
<#else>
import com.tkfc.boot.starter.mybatis.extend.BaseMapper;
</#if>
import ${modelPackageName}.${modelName};

/**
* ${description} Dao
*
* @author ${author}
* @since ${createTime}
*/

<#if isSplitTable>
public interface ${modelName}Mapper extends BaseSplitMapper<${modelName}> {
<#else>
public interface ${modelName}Mapper extends BaseMapper<${modelName}> {
</#if>

}
