package com.tkfc.boot.starter.mybatis.extend;

import com.alibaba.fastjson2.JSONArray;
import com.tkfc.boot.starter.mybatis.sql.build.SqlBuilder;
import com.tkfc.boot.starter.mybatis.sql.wrapper.SqlWrapper;
import com.tkfc.boot.starter.mybatis.sql.wrapper.interfaces.IWrapper;
import com.tkfc.core.common.pojo.BasePageExpressionRequest;
import com.tkfc.core.common.pojo.BaseResponse;
import com.tkfc.core.common.pojo.Pagination;
import com.tkfc.core.function.SerializableConsumer;
import com.tkfc.core.toolkit.CollectionUtil;
import com.tkfc.core.toolkit.ParseUtil;
import com.tkfc.core.toolkit.ReflectionUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * @param <T>
 * @author 0neBean
 * 顶级service impl
 */
public abstract class BaseSplitServiceImpl<T extends BaseModel, V extends BaseVo<T>, K extends BaseSplitMapper<T>> implements BaseSplitService<T, V> {


    /**
     * dao原型属性
     */
    @Autowired
    @SuppressWarnings("all")
    protected K baseMapper;

    @Override
    public Integer delete(IWrapper wrapper, String tableSuffix) {
        return baseMapper.delete(wrapper, tableSuffix);
    }

    @Override
    public Integer deleteById(Long id, String tableSuffix) {
        return baseMapper.delete(SqlBuilder.<T>init().eq(BaseModel::getId, id).orderByDesc(BaseModel::getId), tableSuffix);
    }

    @Override
    public Integer deleteByIds(List<Long> ids, String tableSuffix) {
        return baseMapper.delete(SqlBuilder.<T>init().in(BaseModel::getId, Collections.singletonList(ids)).orderByDesc(BaseModel::getId), tableSuffix);
    }

    @Override
    public Integer deletePhysically(IWrapper wrapper, String tableSuffix) {
        return baseMapper.deletePhysically(wrapper, tableSuffix);
    }

    @Override
    public Integer deleteByIdPhysically(Long id, String tableSuffix) {
        return baseMapper.deletePhysically(SqlBuilder.<T>init().eq(BaseModel::getId, id).orderByDesc(BaseModel::getId), tableSuffix);
    }

    @Override
    public Integer deleteByIdsPhysically(List<Long> ids, String tableSuffix) {
        return baseMapper.deletePhysically(SqlBuilder.<T>init().in(BaseModel::getId, Collections.singletonList(ids)).orderByDesc(BaseModel::getId), tableSuffix);
    }

    @Override
    public Integer save(T entity, String tableSuffix) {
        if (Objects.nonNull(entity.getId()) && Objects.nonNull(this.findById(entity.getId(), tableSuffix))) {
            return baseMapper.update(entity, SqlBuilder.<T>init().eq(BaseModel::getId, entity.getId()), tableSuffix);
        } else {
            return baseMapper.saveBatch(Collections.singletonList(entity), tableSuffix);
        }
    }

    @Override
    public Integer saveOnDuplicateKeyUpdate(T entity, String tableSuffix) {
        return baseMapper.saveBatchOnDuplicateKeyUpdate(Collections.singletonList(entity), tableSuffix);
    }

    @Override
    public Integer saveBatch(List<T> batch, String tableSuffix) {
        return baseMapper.saveBatch(batch, tableSuffix);
    }

    @Override
    public Integer saveBatchOnDuplicateKeyUpdate(List<T> batch, String tableSuffix) {
        return baseMapper.saveBatchOnDuplicateKeyUpdate(batch, tableSuffix);
    }

    @Override
    public Integer update(T entity, IWrapper wrapper, String tableSuffix) {
        return baseMapper.update(entity, wrapper, tableSuffix);
    }

    @Override
    public Integer updateBatch(List<T> batch, String tableSuffix) {
        return baseMapper.updateBatch(batch, tableSuffix);
    }

    @Override
    public Long getMaxId(String tableSuffix) {
        return baseMapper.getMaxId(tableSuffix);
    }

    @Override
    public Long count(IWrapper wrapper, String tableSuffix) {
        return baseMapper.count(wrapper, tableSuffix);
    }

    @Override
    public List<T> find(IWrapper wrapper, String tableSuffix) {
        return baseMapper.find(wrapper, tableSuffix);
    }

    @Override
    public T findOne(IWrapper wrapper, String tableSuffix) {
        wrapper.getPagination().setCurrentPage(Pagination.DEFAULT_CURRENT_PAGE);
        wrapper.getPagination().setPageSize(Pagination.DEFAULT_CURRENT_PAGE);
        return baseMapper.find(wrapper, tableSuffix).get(0);
    }

    @Override
    public BaseResponse<List<V>> findPage(BasePageExpressionRequest request, String tableSuffix, String... allowFields) {
        return findPage(request, tableSuffix,null, allowFields);
    }

    @Override
    public BaseResponse<List<V>> findPage(BasePageExpressionRequest request, String tableSuffix, SerializableConsumer<SqlWrapper<T>> sqlConsumer, String... allowFields) {
        SqlWrapper<T> sql = SqlBuilder.<T>init().expression(request,allowFields);
        if (Objects.nonNull(sqlConsumer)) {
            sqlConsumer.accept(sql);
        }
        List<T> lists = find(sql, tableSuffix);
        List<V> vos = toVos(lists);
        return BaseResponse.ok(vos, sql.getPagination());
    }

    @Override
    public List<T> findAll(String tableSuffix) {
        return baseMapper.find(SqlBuilder.<BaseModel>init().orderByDesc(BaseModel::getId), tableSuffix);
    }

    @Override
    public T findById(Long id, String tableSuffix) {
        return Optional
                .ofNullable(baseMapper.find(SqlBuilder.<T>init().eq(BaseModel::getId, id).orderByDesc(BaseModel::getId), tableSuffix))
                .orElse(new ArrayList<>()).stream().findFirst().orElse(null);
    }

    @Override
    public List<T> findByIds(List<Long> ids, String tableSuffix) {
        return baseMapper.find(SqlBuilder.<T>init().in(BaseModel::getId, Collections.singletonList(ids)).orderByDesc(BaseModel::getId), tableSuffix);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<V> toVos(List<T> dataList) {
        if (CollectionUtil.isEmpty(dataList)) {
            return new ArrayList<>();
        }
        String json = JSONArray.toJSONString(dataList);
        Class<?> clazz = ReflectionUtil.findParameterizedType(this.getClass(), 1);
        return (List<V>) JSONArray.parseArray(json, clazz);
    }

    @Override
    @SuppressWarnings("all")
    public List<V> toVos(List<T> dataList, BiFunction<Field, Object, Object> biFunction) {
        if (CollectionUtil.isEmpty(dataList)) {
            return new ArrayList<>();
        }
        String json = JSONArray.toJSONString(dataList);
        Class<?> clazz = ReflectionUtil.findParameterizedType(this.getClass(), 1);
        List<V> vos = (List<V>) JSONArray.parseArray(json, clazz);
        return vos.stream().peek(v -> {
            List<Field> fields = ReflectionUtil.getAccessibleFields(v.getClass());
            for (Field field : fields) {
                String fieldName = field.getName();
                Object fieldValue = ReflectionUtil.getFieldValue(v, fieldName);
                Object result = biFunction.apply(field, fieldValue);
                if (Objects.nonNull(result)) {
                    ReflectionUtil.setFieldValue(v, fieldName, result);
                    String rawFeildName = fieldName + "Raw";
                    Class<?> rawFieldDeclaringClass = Optional.ofNullable(ReflectionUtil.getField(v, rawFeildName)).map(Field::getType).orElse(null);
                    if (Objects.nonNull(rawFieldDeclaringClass)) {
                        Object rawValue = ParseUtil.toBasicTypeValue(fieldValue, rawFieldDeclaringClass);
                        ReflectionUtil.setFieldValueIfExits(v, rawFeildName, rawValue);
                    }
                }
            }
        }).collect(Collectors.toList());

    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> toModels(List<V> dataList) {
        String json = JSONArray.toJSONString(dataList);
        Class<?> clazz = ReflectionUtil.findParameterizedType(this.getClass(), 0);
        return (List<T>) JSONArray.parseArray(json, clazz);
    }

    @Override
    @SuppressWarnings("all")
    public List<T> toModels(List<V> dataList, BiFunction<Field, Object, Object> biFunction) {
        String json = JSONArray.toJSONString(dataList);
        Class<?> clazz = ReflectionUtil.findParameterizedType(this.getClass(), 0);
        List<T> vos = (List<T>) JSONArray.parseArray(json, clazz);
        return vos.stream().peek(v -> {
            List<Field> fields = ReflectionUtil.getAccessibleFields(v.getClass());
            for (Field field : fields) {
                String fieldName = field.getName();
                Object fieldValue = ReflectionUtil.getFieldValue(v, fieldName);
                Object result = biFunction.apply(field, fieldValue);
                if (Objects.nonNull(result)) {
                    ReflectionUtil.setFieldValue(v, fieldName, result);
                }
            }
        }).collect(Collectors.toList());

    }
}
