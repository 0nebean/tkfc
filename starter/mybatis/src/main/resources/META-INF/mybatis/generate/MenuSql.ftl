-- [ ${description} ] 菜单sql:

-- 功能上级菜单ID
SELECT @moduleParentId := '1';
-- 菜单 SQL
INSERT INTO `biz_permission` (`is_root`, `parent_id`, `show_type`, `sort`, `icon`, `ch_name`, `perm_id`, `permission_type`, `permission_url`, `component_name`, `component_path`)
VALUES ('0', @moduleParentId, '0', 0, '${icon}', '${description}', '${permShortName}', 'route', '/${componentPath}/${modelVarName}', '${modelName}List', '/view/${componentPath}/${modelVarName}/${modelName}List');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
INSERT INTO `biz_permission` (`is_root`, `parent_id`, `show_type`, `sort`, `ch_name`, `perm_id`, `permission_type`)
VALUES ('0', @parentId, '0', 0, '${description}列表', '${permShortName}_PAGE', 'button');

INSERT INTO `biz_permission` (`is_root`, `parent_id`, `show_type`, `sort`, `ch_name`, `perm_id`, `permission_type`)
VALUES ('0', @parentId, '0', 1, '查看${description}', '${permShortName}_VIEW', 'button');

INSERT INTO `biz_permission` (`is_root`, `parent_id`, `show_type`, `sort`, `ch_name`, `perm_id`, `permission_type`)
VALUES ('0', @parentId, '0', 2, '保存${description}', '${permShortName}_SAVE', 'button');

INSERT INTO `biz_permission` (`is_root`, `parent_id`, `show_type`, `sort`, `ch_name`, `perm_id`, `permission_type`)
VALUES ('0', @parentId, '0', 3, '删除${description}', '${permShortName}_DEL', 'button');

INSERT INTO `biz_permission` (`is_root`, `parent_id`, `show_type`, `sort`, `ch_name`, `perm_id`, `permission_type`)
VALUES ('0', @parentId, '0', 3, '导出${description}', '${permShortName}_EXPORT', 'button');
