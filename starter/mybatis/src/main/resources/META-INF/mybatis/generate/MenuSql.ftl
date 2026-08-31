-- [ ${description} ] 菜单sql:

-- 功能上级菜单ID
SELECT @moduleParentId := '1';

-- 三级目录（业务分组）
INSERT INTO `biz_permission` (`is_root`, `parent_id`, `show_type`, `sort`, `icon`, `ch_name`, `perm_id`, `permission_type`, `permission_url`, `component_name`, `component_path`)
VALUES ('0', @moduleParentId, '0', 0, '', '${description}', '', 'folder_lv3', '/${componentPath}/${modelVarName}', '', '');

-- 业务分组ID（页面路由与按钮的父级）
SELECT @parentId := LAST_INSERT_ID();

-- 页面路由 SQL
INSERT INTO `biz_permission` (`is_root`, `parent_id`, `show_type`, `sort`, `icon`, `ch_name`, `perm_id`, `permission_type`, `permission_url`, `component_name`, `component_path`)
VALUES ('0', @parentId, '0', 0, '${icon}', '${description}-页面路由', '${permShortName}_ROUTER', 'route', '/${componentPath}/${modelVarName}', '${modelName}List', '/view/${componentPath}/${modelVarName}/${modelName}List');

-- 按钮 SQL
INSERT INTO `biz_permission` (`is_root`, `parent_id`, `show_type`, `sort`, `ch_name`, `perm_id`, `permission_type`)
VALUES ('0', @parentId, '0', 1, '${description}列表', '${permShortName}_PAGE', 'button');

INSERT INTO `biz_permission` (`is_root`, `parent_id`, `show_type`, `sort`, `ch_name`, `perm_id`, `permission_type`)
VALUES ('0', @parentId, '0', 2, '查看${description}', '${permShortName}_VIEW', 'button');

INSERT INTO `biz_permission` (`is_root`, `parent_id`, `show_type`, `sort`, `ch_name`, `perm_id`, `permission_type`)
VALUES ('0', @parentId, '0', 3, '保存${description}', '${permShortName}_SAVE', 'button');

INSERT INTO `biz_permission` (`is_root`, `parent_id`, `show_type`, `sort`, `ch_name`, `perm_id`, `permission_type`)
VALUES ('0', @parentId, '0', 4, '删除${description}', '${permShortName}_DEL', 'button');

INSERT INTO `biz_permission` (`is_root`, `parent_id`, `show_type`, `sort`, `ch_name`, `perm_id`, `permission_type`)
VALUES ('0', @parentId, '0', 5, '导出${description}', '${permShortName}_EXPORT', 'button');

-- 字典 SQL（枚举字段）
<#if enumFieldArr?exists && (enumFieldArr?size > 0)>
<#list enumFieldArr as enumField>
<#if enumField.enumItems?exists && (enumField.enumItems?size > 0)>
<#list enumField.enumItems as enumItem>
INSERT INTO `biz_config_dictionary` (`tenant_id`, `val`, `dic`, `group_val`, `group_dic`, `sort`)
VALUES (0, '${enumItem.value?replace("'","''")}', '${enumItem.description?replace("'","''")}', '${enumField.enumGroupVal}', '${enumField.enumGroupDic?replace("'","''")}', ${enumItem.sort});
</#list>

</#if>
</#list>
</#if>
