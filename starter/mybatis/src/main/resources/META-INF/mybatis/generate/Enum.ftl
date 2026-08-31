package ${enumPackageName};

import com.tkfc.core.enums.base.BaseEnums;

/**
 * ${enumDescription}
 *
 * @author ${author}
 * @since ${createTime}
 */
public enum ${enumClassName} implements BaseEnums<String> {

    // 枚举项
<#if enumItems?exists>
    <#list enumItems as item>
    ${item.name}("${item.value}", "${item.description}", ${item.sort})<#if item_has_next>,<#else>;</#if>
    </#list>
</#if>

    private final String description;
    private final String value;
    private final Integer sort;

    ${enumClassName}(String value, String description, Integer sort) {
        this.value = value;
        this.description = description;
        this.sort = sort;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public Integer getSort() {
        return sort;
    }

    @Override
    public BaseEnums<String>[] getValues() {
        return values();
    }

}
