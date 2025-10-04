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
public interface BaseService<T extends BaseModel, V extends BaseVo<T>> {

    Integer delete(IWrapper wrapper);

    Integer deleteById(Long id);

    Integer deleteByIds(List<Long> ids);

    Integer deletePhysically(IWrapper wrapper);

    Integer deleteByIdPhysically(Long id);

    Integer deleteByIdsPhysically(List<Long> ids);

    Integer save(T entity);

    Integer saveOnDuplicateKeyUpdate(T entity);

    Integer saveBatch(List<T> batch);

    Integer saveBatchOnDuplicateKeyUpdate(List<T> batch);

    Integer update(T entity);

    Integer update(T entity, IWrapper wrapper);

    Integer updateBatch(List<T> batch);

    Long getMaxId();

    Long count(IWrapper wrapper);

    List<T> find(IWrapper wrapper);

    T findOne(IWrapper wrapper);

    BaseResponse<List<V>> findPage(BasePageExpressionRequest request,String...allowFields);

    BaseResponse<List<V>> findPage(BasePageExpressionRequest request, SerializableConsumer<SqlWrapper<T>> sqlConsumer,String...allowFields);

    List<T> findAll();

    T findById(Long id);

    List<T> findByIds(List<Long> ids);

    List<V> toVos(List<T> dataList);

    List<V> toVos(List<T> dataList, BiFunction<Field, Object, Object> biFunction);

    List<T> toModels(List<V> dataList);

    List<T> toModels(List<V> dataList, BiFunction<Field, Object, Object> biFunction);
}
