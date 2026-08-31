package ${servicePackageName}.impl;

<#if isSplitTable>
    import com.tkfc.boot.starter.mybatis.extend.BaseSplitServiceImpl;
<#else>
import com.tkfc.boot.starter.mybatis.extend.BaseServiceImpl;
</#if>
import org.springframework.stereotype.Service;
import ${modelPackageName}.${modelName};
import ${voPackageName}.${modelName}Vo;
import ${servicePackageName}.${modelName}Service;
import ${daoPackageName}.${modelName}Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
<#if geneDataType == "tree">
import com.tkfc.boot.starter.mybatis.sql.build.SqlBuilder;
import com.tkfc.boot.starter.mybatis.sql.wrapper.SqlWrapper;
import com.tkfc.core.enums.YesOrNoEnum;
import com.tkfc.core.toolkit.CollectionUtil;
import com.tkfc.dictionary.util.DictionaryUtil;
import ${voPackageName}.${modelName}Tree;

import java.util.*;
import java.util.stream.Collectors;
<#else>

</#if>

/**
 * ${description} serviceImpl
 *
 * @author ${author}
 * @since ${createTime}
 */
@Slf4j
@Service
@RequiredArgsConstructor
<#if isSplitTable>
public class ${modelName}ServiceImpl extends BaseSplitServiceImpl<${modelName}, ${modelName}Vo, ${modelName}Mapper> implements ${modelName}Service{
<#else>
public class ${modelName}ServiceImpl extends BaseServiceImpl<${modelName}, ${modelName}Vo, ${modelName}Mapper> implements ${modelName}Service{
</#if>

<#if geneDataType == "tree">

    @Override
    public List<${modelName}Tree> findChildAsync(<#if isSplitTable>String tenantId, </#if>Long parentId, Set<Long> selectedIds, String[] expressions, String[] allowFields) {
        SqlWrapper<${modelName}> sql = SqlBuilder.init();
        sql.expression(expressions, allowFields);
        boolean emptyParentId = Objects.isNull(parentId);
        sql.eq(!emptyParentId, ${modelName}::getParentId,parentId);
        sql.eq(emptyParentId, ${modelName}::getIsRoot,YesOrNoEnum.YES.getValue());
        sql.orderByAsc(${modelName}::getSort);
        List<${modelName}> modelDataList = find(sql<#if isSplitTable>, tenantId</#if>);
        return tree(parentId, modelDataList, selectedIds);
    }

    @Override
    public List<${modelName}Tree> findChildSync(<#if isSplitTable>String tenantId, </#if>Long selfId, Set<Long> selectedIds, String[] expressions, String[] allowFields) {
        SqlWrapper<${modelName}> sql = SqlBuilder.init();
        sql.expression(expressions, allowFields);
        sql.orderByAsc(${modelName}::getSort);
        sql.queryAll();
        List<${modelName}> queryList = find(sql<#if isSplitTable>, tenantId</#if>);
        queryList = queryList.stream().filter(item -> !Objects.equals(selfId, item.getId())).collect(Collectors.toList());
        return tree(null, queryList, selectedIds);
    }

    /**
     * 递归包装树形结构
     *
     * @param parentId    父id
     * @param originals   原始结构id
     * @param selectedIds 选中的ids
     * @return 树形结构
     */
    private List<${modelName}Tree> tree(Long parentId, List<${modelName}> originals, Set<Long> selectedIds) {
        ${modelName}Tree _final = null;
        if (CollectionUtil.isEmpty(originals)) {
            return Collections.emptyList();
        }
        for (${modelName} original : originals) {
            //如果是根节点,放入最终的结果中剩余数量减一
            if (Objects.equals(original.getIsRoot(), YesOrNoEnum.YES.getValue())) {
                _final = original.toVo(${modelName}Tree.class);
            }
        }
        if (Objects.isNull(_final)) {
            ${modelName} minIdPrem = Collections.min(originals, Comparator.comparingLong(${modelName}::getId));
            _final = minIdPrem.toVo(${modelName}Tree.class);
        }
        //如果是异步树 只查一层 ,需要指定一个临时的 父节点
        boolean onlyChild = Objects.nonNull(parentId);
        _final = onlyChild ? new ${modelName}Tree(YesOrNoEnum.YES.getValue(), parentId) : _final;
        treeChild(_final, originals, selectedIds);
        return onlyChild ? _final.getChildren() : Collections.singletonList(_final);
    }

    /**
     * 包装数据节点的树形
     *
     * @param treeItem    树形节点
     * @param selectedIds 选中的ids
     */
    private void handleTreeItemAttr(${modelName}Tree treeItem, Set<Long> selectedIds) {
        treeItem.setChecked(selectedIds.contains(treeItem.getId()) && !treeItem.getHasChild());
        treeItem.setHasChild(CollectionUtil.isNotEmpty(treeItem.getChildren()));
        treeItem.set_showChildren(treeItem.getHasChild());
        treeItem.setExpand(treeItem.getHasChild());
        //treeItem.setIsActive(DictionaryUtil.dic("IsActiveEnum", treeItem.getIsActive()));
        treeItem.setCreateTime(treeItem.getCreateTime());
        treeItem.setUpdateTime(treeItem.getUpdateTime());
        treeItem.setChName(String.format(" %s", treeItem.getChName()));
        treeItem.setTitle(treeItem.getChName());
        treeItem.setValue(treeItem.getId());
        treeItem.set_loading(Boolean.FALSE);
    }

    /**
     * 包装方法递归子方法
     *
     * @param vo          tree数据
     * @param originals   元数据
     * @param selectedIds 选中节点的ID
     */
    private void treeChild(${modelName}Tree vo, List<${modelName}> originals, Set<Long> selectedIds) {
        List<${modelName}Tree> childList = new ArrayList<>();
        for (${modelName} original : originals) {
            boolean isChild = Objects.nonNull(vo.getId()) && Objects.nonNull(original.getParentId()) && Objects.equals(vo.getId(), original.getParentId());
            if (isChild) {
                ${modelName}Tree treeItem = original.toVo(${modelName}Tree.class);
                childList.add(treeItem);
            }
        }
        vo.setChildren((CollectionUtil.isNotEmpty(childList)) ? childList : null);
        if (CollectionUtil.isNotEmpty(vo.getChildren())) {
            for (${modelName}Tree child : vo.getChildren()) {
                treeChild(child, originals, selectedIds);
            }
        }
        handleTreeItemAttr(vo, selectedIds);
    }
</#if>
}
