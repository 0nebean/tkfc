package com.tkfc.boot.starter.mybatis.extend;

import com.tkfc.boot.starter.mybatis.sql.wrapper.interfaces.IWrapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @param <T>
 * @author 0neBean
 * 顶级mybatis mapper
 */
public interface BaseSplitMapper<T extends BaseModel> extends SqlMapper {


    /**
     * 批量更新
     *
     * @param list        列表
     * @param tableSuffix 表名后缀
     * @return 影响的行数
     */
    Integer updateBatch(@Param("list") List<T> list, @Param("tableSuffix") String tableSuffix);

    /**
     * 条件更新
     *
     * @param entity      实体
     * @param sqlWrapper  条件封装
     * @param tableSuffix 表名后缀
     * @return 影响的行数
     */
    Integer update(@Param("entity") T entity, @Param("sqlWrapper") IWrapper sqlWrapper, @Param("tableSuffix") String tableSuffix);

    /**
     * 条件删除
     *
     * @param sqlWrapper  条件封装
     * @param tableSuffix 表名后缀
     * @return 影响的行数
     */
    Integer delete(@Param("sqlWrapper") IWrapper sqlWrapper, @Param("tableSuffix") String tableSuffix);

    /**
     * 物理条件删除
     *
     * @param sqlWrapper  条件封装
     * @param tableSuffix 表名后缀
     * @return 影响的行数
     */
    Integer deletePhysically(@Param("sqlWrapper") IWrapper sqlWrapper, @Param("tableSuffix") String tableSuffix);

    /**
     * 批量保存
     *
     * @param list        列表
     * @param tableSuffix 表名后缀
     * @return 影响的行数
     */
    Integer saveBatch(@Param("list") List<T> list, @Param("tableSuffix") String tableSuffix);

    /**
     * 批量保存/更新 不返回ID
     *
     * @param list 列表
     * @param tableSuffix 表名后缀
     * @return 影响的行数
     */
    Integer saveBatchOnDuplicateKeyUpdate(@Param("list") List<T> list, @Param("tableSuffix") String tableSuffix);

    /**
     * 条件查询
     *
     * @param sqlWrapper  条件封装
     * @param tableSuffix 表名后缀
     * @return list
     */
    List<T> find(@Param("sqlWrapper") IWrapper sqlWrapper, @Param("tableSuffix") String tableSuffix);

    /**
     * 计数
     *
     * @param sqlWrapper  条件封装
     * @param tableSuffix 表名后缀
     * @return list
     */
    Long count(@Param("sqlWrapper") IWrapper sqlWrapper, @Param("tableSuffix") String tableSuffix);

    /**
     * 获取最大的ID
     *
     * @param tableSuffix 表名后缀
     * @return id
     */
    Long getMaxId(@Param("tableSuffix") String tableSuffix);
}
