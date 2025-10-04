package com.tkfc.boot.starter.mybatis.extend;

import com.tkfc.boot.starter.mybatis.sql.wrapper.SqlWrapper;
import com.tkfc.boot.starter.mybatis.sql.wrapper.interfaces.IWrapper;
import com.tkfc.core.common.pojo.BasePageExpressionRequest;
import com.tkfc.core.common.pojo.BaseResponse;
import com.tkfc.core.function.SerializableConsumer;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @param <T>
 * @author 0neBean
 * 顶级service
 */
public interface BaseSplitService<T extends BaseModel, V extends BaseVo<T>> {

    Integer delete(IWrapper wrapper, String tableSuffix);

    Integer deleteById(Long id, String tableSuffix);

    Integer deleteByIds(List<Long> ids, String tableSuffix);

    Integer deletePhysically(IWrapper wrapper, String tableSuffix);

    Integer deleteByIdPhysically(Long id, String tableSuffix);

    Integer deleteByIdsPhysically(List<Long> ids, String tableSuffix);

    Integer save(T entity, String tableSuffix);

    Integer saveOnDuplicateKeyUpdate(T entity, String tableSuffix);

    Integer saveBatch(List<T> batch, String tableSuffix);

    Integer saveBatchOnDuplicateKeyUpdate(List<T> batch, String tableSuffix);

    Integer update(T entity, IWrapper wrapper, String tableSuffix);

    Integer updateBatch(List<T> batch, String tableSuffix);

    Long getMaxId(String tableSuffix);

    Long count(IWrapper wrapper, String tableSuffix);

    List<T> find(IWrapper wrapper, String tableSuffix);

    BaseResponse<List<V>> findPage(BasePageExpressionRequest request, String tableSuffix, String... allowFields);

    BaseResponse<List<V>> findPage(BasePageExpressionRequest request, String tableSuffix, SerializableConsumer<SqlWrapper<T>> sqlConsumer, String... allowFields);

    T findOne(IWrapper wrapper, String tableSuffix);

    List<T> findAll(String tableSuffix);

    T findById(Long id, String tableSuffix);

    List<T> findByIds(List<Long> ids, String tableSuffix);

    List<V> toVos(List<T> dataList);

    List<V> toVos(List<T> dataList, BiFunction<Field, Object, Object> biFunction);

    List<T> toModels(List<V> dataList);

    List<T> toModels(List<V> dataList, BiFunction<Field, Object, Object> biFunction);
}
