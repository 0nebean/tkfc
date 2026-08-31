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
 * @author 0neBean 顶级service impl
 */
public abstract class BaseServiceImpl<T extends BaseModel, V extends BaseVo<T>, K extends BaseMapper<T>> implements BaseService<T, V> {

    /**
     * dao原型属性
     */
    @Autowired
    @SuppressWarnings("all")
    protected K baseMapper;

    @Override
    public Integer delete(IWrapper wrapper) {
        return baseMapper.delete(wrapper);
    }

    @Override
    public Integer deleteById(Long id) {
        return baseMapper
                .delete(SqlBuilder.<T>init().eq(BaseModel::getId, id).orderByDesc(BaseModel::getId).withOutPrefix());
    }

    @Override
    public Integer deleteByIds(List<Long> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return 0;
        }
        return baseMapper.delete(SqlBuilder.<T>init().in(BaseModel::getId, ids).orderByDesc(BaseModel::getId).withOutPrefix());
    }

    @Override
    public Integer deletePhysically(IWrapper wrapper) {
        return baseMapper.deletePhysically(wrapper.withOutPrefix());
    }

    @Override
    public Integer deleteByIdPhysically(Long id) {
        return baseMapper.deletePhysically(
                SqlBuilder.<T>init().eq(BaseModel::getId, id).orderByDesc(BaseModel::getId).withOutPrefix());
    }

    @Override
    public Integer deleteByIdsPhysically(List<Long> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return 0;
        }
        return baseMapper.deletePhysically(SqlBuilder.<T>init().in(BaseModel::getId, ids).orderByDesc(BaseModel::getId).withOutPrefix());
    }

    @Override
    public Integer save(T entity) {
        if (Objects.nonNull(entity.getId()) && Objects.nonNull(this.findById(entity.getId()))) {
            return baseMapper.update(entity, SqlBuilder.<T>init().eq(BaseModel::getId, entity.getId()).withOutPrefix());
        } else {
            return baseMapper.saveBatch(Collections.singletonList(entity));
        }
    }

    @Override
    public Integer saveOnDuplicateKeyUpdate(T entity) {
        return baseMapper.saveBatchOnDuplicateKeyUpdate(Collections.singletonList(entity));
    }

    @Override
    public Integer saveBatch(List<T> batch) {
        if (CollectionUtil.isEmpty(batch)) {
            return 0;
        }
        return baseMapper.saveBatch(batch);
    }

    @Override
    public Integer saveBatchOnDuplicateKeyUpdate(List<T> batch) {
        if (CollectionUtil.isEmpty(batch)) {
            return 0;
        }
        return baseMapper.saveBatchOnDuplicateKeyUpdate(batch);
    }

    @Override
    public Integer update(T entity) {
        return baseMapper.update(entity, SqlBuilder.<T>init().eq(BaseModel::getId, entity.getId()).withOutPrefix());
    }

    @Override
    public Integer update(T entity, IWrapper wrapper) {
        return baseMapper.update(entity, wrapper.withOutPrefix());
    }

    @Override
    public Integer updateBatch(List<T> batch) {
        if (CollectionUtil.isEmpty(batch)) {
            return 0;
        }
        return baseMapper.updateBatch(batch);
    }

    @Override
    public Long getMaxId() {
        return baseMapper.getMaxId();
    }

    @Override
    public Long count(IWrapper wrapper) {
        return baseMapper.count(wrapper);
    }

    @Override
    public List<T> find(IWrapper wrapper) {
        return baseMapper.find(wrapper, (wrapper.getHasPagination()) ? wrapper.getPagination() : null);
    }

    @Override
    public T findOne(IWrapper wrapper) {
        wrapper.getPagination().setCurrentPage(Pagination.DEFAULT_CURRENT_PAGE);
        wrapper.getPagination().setPageSize(Pagination.DEFAULT_CURRENT_PAGE);
        return baseMapper.find(wrapper).stream().filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public BaseResponse<List<V>> findPage(BasePageExpressionRequest request, String... allowFields) {
        return findPage(request, null, allowFields);
    }

    @Override
    public BaseResponse<List<V>> findPage(BasePageExpressionRequest request, SerializableConsumer<SqlWrapper<T>> sqlConsumer, String... allowFields) {
        SqlWrapper<T> sql = SqlBuilder.<T>init().expression(request, allowFields);
        if (Objects.nonNull(sqlConsumer)) {
            sqlConsumer.accept(sql);
        }
        List<T> lists = find(sql);
        List<V> vos = toVos(lists);
        return BaseResponse.ok(vos, sql.getPagination());
    }

    @Override
    public List<T> findAll() {
        return baseMapper.find(SqlBuilder.<BaseModel>init().orderByDesc(BaseModel::getId));
    }

    @Override
    public T findById(Long id) {
        return Optional
                .ofNullable(baseMapper.find(SqlBuilder.<T>init().eq(BaseModel::getId, id).orderByDesc(BaseModel::getId)))
                .orElse(new ArrayList<>()).stream().findFirst().orElse(null);

    }

    @Override
    public List<T> findByIds(List<Long> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return new ArrayList<>();
        }
        return baseMapper.find(SqlBuilder.<T>init().in(BaseModel::getId, ids).orderByDesc(BaseModel::getId));
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
